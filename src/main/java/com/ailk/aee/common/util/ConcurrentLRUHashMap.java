// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.AbstractCollection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.AbstractSet;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

@CVSID("$Id: ConcurrentLRUHashMap.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConcurrentLRUHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    private static final long serialVersionUID = -5031526786765467550L;
    static final int DEFAULT_SEGEMENT_MAX_CAPACITY = 100;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    transient Set<K> keySet;
    transient Set<Map.Entry<K, V>> entrySet;
    transient Collection<V> values;
    
    private static int hash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }
    
    public ConcurrentLRUHashMap() {
        this(100, 0.75f, 16);
    }
    
    public ConcurrentLRUHashMap(final int segementCapacity) {
        this(segementCapacity, 0.75f, 16);
    }
    
    public ConcurrentLRUHashMap(final int segementCapacity, final float loadFactor) {
        this(segementCapacity, loadFactor, 16);
    }
    
    public ConcurrentLRUHashMap(final int segementCapacity, final float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || segementCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
        }
        int sshift = 0;
        int ssize;
        for (ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
            ++sshift;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<K, V>(segementCapacity, loadFactor, this);
        }
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].clear();
        }
    }
    
    public boolean contains(final Object value) {
        return this.containsValue(value);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).containsKey(key, hash);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Segment<K, V>[] segments = this.segments;
        final int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                final int n = mcsum;
                final int[] array = mc;
                final int n2 = i;
                final int modCount = segments[i].modCount;
                array[n2] = modCount;
                mcsum = n + modCount;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int j = 0; j < segments.length; ++j) {
                    if (mc[j] != segments[j].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        for (int l = 0; l < segments.length; ++l) {
            segments[l].lock();
        }
        boolean found = false;
        try {
            for (int m = 0; m < segments.length; ++m) {
                if (segments[m].containsValue(value)) {
                    found = true;
                    break;
                }
            }
        }
        finally {
            for (int i2 = 0; i2 < segments.length; ++i2) {
                segments[i2].unlock();
            }
        }
        return found;
    }
    
    public Enumeration<V> elements() {
        return new ValueIterator();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet());
    }
    
    @Override
    public V get(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).get(key, hash);
    }
    
    @Override
    public boolean isEmpty() {
        final Segment<K, V>[] segments = this.segments;
        final int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            final int n = mcsum;
            final int[] array = mc;
            final int n2 = i;
            final int modCount = segments[i].modCount;
            array[n2] = modCount;
            mcsum = n + modCount;
        }
        if (mcsum != 0) {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Enumeration<K> keys() {
        return new KeyIterator();
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet());
    }
    
    @Override
    public V put(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, false);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, true);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].setTable(new HashEntry[1]);
        }
        while (true) {
            final K key = (K)s.readObject();
            final V value = (V)s.readObject();
            if (key == null) {
                break;
            }
            this.put(key, value);
        }
    }
    
    @Override
    public V remove(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, null);
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        final int hash = hash(key.hashCode());
        return value != null && this.segmentFor(hash).remove(key, hash, value) != null;
    }
    
    @Override
    public V replace(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, value);
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }
    
    final Segment<K, V> segmentFor(final int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }
    
    @Override
    public int size() {
        final Segment<K, V>[] segments = this.segments;
        long sum = 0L;
        long check = 0L;
        final int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            check = 0L;
            sum = 0L;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                sum += segments[i].count;
                final int n = mcsum;
                final int[] array = mc;
                final int n2 = i;
                final int modCount = segments[i].modCount;
                array[n2] = modCount;
                mcsum = n + modCount;
            }
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        check = -1L;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            sum = 0L;
            for (int j = 0; j < segments.length; ++j) {
                segments[j].lock();
            }
            for (int j = 0; j < segments.length; ++j) {
                sum += segments[j].count;
            }
            for (int j = 0; j < segments.length; ++j) {
                segments[j].unlock();
            }
        }
        if (sum > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> vs = this.values;
        return (vs != null) ? vs : (this.values = new Values());
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int k = 0; k < this.segments.length; ++k) {
            final Segment<K, V> seg = this.segments[k];
            seg.lock();
            try {
                final HashEntry<K, V>[] tab = seg.table;
                for (int i = 0; i < tab.length; ++i) {
                    for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                        s.writeObject(e.key);
                        s.writeObject(e.value);
                    }
                }
            }
            finally {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }
    
    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            final HashEntry<K, V> e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            final V v = ConcurrentLRUHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            return ConcurrentLRUHashMap.this.remove(e.getKey(), e.getValue());
        }
        
        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }
    }
    
    static final class HashEntry<K, V>
    {
        final K key;
        final int hash;
        volatile V value;
        final HashEntry<K, V> next;
        HashEntry<K, V> linkNext;
        HashEntry<K, V> linkPrev;
        AtomicBoolean dead;
        
        static final <K, V> HashEntry<K, V>[] newArray(final int i) {
            return (HashEntry<K, V>[])new HashEntry[i];
        }
        
        HashEntry(final K key, final int hash, final HashEntry<K, V> next, final V value) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
            this.dead = new AtomicBoolean(false);
        }
    }
    
    abstract class HashIterator
    {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;
        
        HashIterator() {
            this.nextSegmentIndex = ConcurrentLRUHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                final Segment<K, V> seg = ConcurrentLRUHashMap.this.segments[this.nextSegmentIndex--];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; --j) {
                        if ((this.nextEntry = this.currentTable[j]) != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }
        
        public boolean hasMoreElements() {
            return this.hasNext();
        }
        
        public boolean hasNext() {
            return this.nextEntry != null;
        }
        
        HashEntry<K, V> nextEntry() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            this.advance();
            return this.lastReturned;
        }
        
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentLRUHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }
    
    final class KeyIterator extends HashIterator implements Iterator<K>, Enumeration<K>
    {
        @Override
        public K next() {
            return super.nextEntry().key;
        }
        
        @Override
        public K nextElement() {
            return super.nextEntry().key;
        }
    }
    
    final class KeySet extends AbstractSet<K>
    {
        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentLRUHashMap.this.containsKey(o);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            return ConcurrentLRUHashMap.this.remove(o) != null;
        }
        
        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }
    }
    
    static final class Segment<K, V> extends ReentrantLock implements Serializable
    {
        private static final long serialVersionUID = 2249069246763182397L;
        transient volatile int count;
        transient int modCount;
        transient int threshold;
        transient volatile HashEntry<K, V>[] table;
        final float loadFactor;
        final transient HashEntry<K, V> header;
        final int maxCapacity;
        
        static final <K, V> Segment<K, V>[] newArray(final int i) {
            return (Segment<K, V>[])new Segment[i];
        }
        
        Segment(final int maxCapacity, final float lf, final ConcurrentLRUHashMap<K, V> lruMap) {
            this.maxCapacity = maxCapacity;
            this.loadFactor = lf;
            this.setTable(HashEntry.newArray(maxCapacity));
            this.header = new HashEntry<K, V>(null, -1, null, null);
            this.header.linkNext = this.header;
            this.header.linkPrev = this.header;
        }
        
        void addBefore(final HashEntry<K, V> newEntry, final HashEntry<K, V> entry) {
            newEntry.linkNext = entry;
            newEntry.linkPrev = entry.linkPrev;
            entry.linkPrev.linkNext = newEntry;
            entry.linkPrev = newEntry;
        }
        
        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    final HashEntry<K, V>[] tab = this.table;
                    for (int i = 0; i < tab.length; ++i) {
                        tab[i] = null;
                    }
                    ++this.modCount;
                    this.count = 0;
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        boolean containsKey(final Object key, final int hash) {
            this.lock();
            try {
                if (this.count != 0) {
                    for (HashEntry<K, V> e = this.getFirst(hash); e != null; e = e.next) {
                        if (e.hash == hash && key.equals(e.key)) {
                            this.moveNodeToHeader(e);
                            return true;
                        }
                    }
                }
                return false;
            }
            finally {
                this.unlock();
            }
        }
        
        boolean containsValue(final Object value) {
            this.lock();
            try {
                if (this.count != 0) {
                    for (HashEntry<K, V> e : this.table) {
                        while (e != null) {
                            V v = e.value;
                            if (v == null) {
                                v = this.readValueUnderLock(e);
                            }
                            if (value.equals(v)) {
                                this.moveNodeToHeader(e);
                                return true;
                            }
                            e = e.next;
                        }
                    }
                }
                return false;
            }
            finally {
                this.unlock();
            }
        }
        
        V get(final Object key, final int hash) {
            this.lock();
            try {
                if (this.count != 0) {
                    HashEntry<K, V> e = this.getFirst(hash);
                    while (e != null) {
                        if (e.hash == hash && key.equals(e.key)) {
                            final V v = e.value;
                            this.moveNodeToHeader(e);
                            if (v != null) {
                                return v;
                            }
                            return (V)this.readValueUnderLock(e);
                        }
                        else {
                            e = e.next;
                        }
                    }
                }
                return null;
            }
            finally {
                this.unlock();
            }
        }
        
        HashEntry<K, V> getFirst(final int hash) {
            final HashEntry<K, V>[] tab = this.table;
            return tab[hash & tab.length - 1];
        }
        
        void moveNodeToHeader(final HashEntry<K, V> entry) {
            this.removeNode(entry);
            this.addBefore(entry, this.header);
        }
        
        V put(final K key, final int hash, final V value, final boolean onlyIfAbsent) {
            this.lock();
            try {
                int c = this.count;
                if (c++ > this.threshold) {
                    this.rehash();
                }
                final HashEntry<K, V>[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry<K, V> e;
                HashEntry<K, V> first;
                for (first = (e = tab[index]); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent) {
                        e.value = value;
                        this.moveNodeToHeader(e);
                    }
                }
                else {
                    oldValue = null;
                    ++this.modCount;
                    final HashEntry<K, V> newEntry = new HashEntry<K, V>(key, hash, first, value);
                    tab[index] = newEntry;
                    this.count = c;
                    this.addBefore(newEntry, this.header);
                    this.removeEldestEntry();
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        V readValueUnderLock(final HashEntry<K, V> e) {
            this.lock();
            try {
                return e.value;
            }
            finally {
                this.unlock();
            }
        }
        
        void rehash() {
            final HashEntry<K, V>[] oldTable = this.table;
            final int oldCapacity = oldTable.length;
            if (oldCapacity >= 1073741824) {
                return;
            }
            final HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int)(newTable.length * this.loadFactor);
            final int sizeMask = newTable.length - 1;
            for (final HashEntry<K, V> e : oldTable) {
                if (e != null) {
                    final HashEntry<K, V> next = e.next;
                    final int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    }
                    else {
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next) {
                            final int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;
                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                            final int k = p.hash & sizeMask;
                            final HashEntry<K, V> n = newTable[k];
                            final HashEntry<K, V> newEntry = new HashEntry<K, V>(p.key, p.hash, n, p.value);
                            newEntry.linkNext = p.linkNext;
                            newEntry.linkPrev = p.linkPrev;
                            newTable[k] = newEntry;
                        }
                    }
                }
            }
            this.table = newTable;
        }
        
        V remove(final Object key, final int hash, final Object value) {
            this.lock();
            try {
                final int c = this.count - 1;
                final HashEntry<K, V>[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry<K, V> e;
                HashEntry<K, V> first;
                for (first = (e = tab[index]); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                V oldValue = null;
                if (e != null) {
                    final V v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next) {
                            newFirst = new HashEntry<K, V>(p.key, p.hash, newFirst, p.value);
                            newFirst.linkNext = p.linkNext;
                            newFirst.linkPrev = p.linkPrev;
                        }
                        tab[index] = newFirst;
                        this.count = c;
                        this.removeNode(e);
                    }
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        void removeEldestEntry() {
            if (this.count > this.maxCapacity) {
                final HashEntry<K, V> eldest = this.header.linkNext;
                this.remove(eldest.key, eldest.hash, null);
            }
        }
        
        void removeNode(final HashEntry<K, V> entry) {
            entry.linkPrev.linkNext = entry.linkNext;
            entry.linkNext.linkPrev = entry.linkPrev;
        }
        
        V replace(final K key, final int hash, final V newValue) {
            this.lock();
            try {
                HashEntry<K, V> e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    e.value = newValue;
                    this.moveNodeToHeader(e);
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        boolean replace(final K key, final int hash, final V oldValue, final V newValue) {
            this.lock();
            try {
                HashEntry<K, V> e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value)) {
                    replaced = true;
                    e.value = newValue;
                    this.moveNodeToHeader(e);
                }
                return replaced;
            }
            finally {
                this.unlock();
            }
        }
        
        void setTable(final HashEntry<K, V>[] newTable) {
            this.threshold = (int)(newTable.length * this.loadFactor);
            this.table = newTable;
        }
    }
    
    final class ValueIterator extends HashIterator implements Iterator<V>, Enumeration<V>
    {
        @Override
        public V next() {
            return super.nextEntry().value;
        }
        
        @Override
        public V nextElement() {
            return super.nextEntry().value;
        }
    }
    
    final class Values extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentLRUHashMap.this.containsValue(o);
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }
    }
    
    final class WriteThroughEntry extends SimpleEntry<K, V>
    {
        private static final long serialVersionUID = -2545938966452012894L;
        
        WriteThroughEntry(final K k, final V v) {
            super(k, v);
        }
        
        @Override
        public V setValue(final V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final V v = super.setValue(value);
            ConcurrentLRUHashMap.this.put(((SimpleEntry<K, V>)this).getKey(), value);
            return v;
        }
    }
}
