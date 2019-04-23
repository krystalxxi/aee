// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.util.List;
import com.ailk.aee.common.util.StringUtils;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import com.ailk.aee.common.conf.MapTools;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ObjectBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ObjectBuilder
{
    private static boolean ignorePropWarn;
    
    public static <T> T build(final Class<T> clazz, final String className, final Map<String, String> prop) throws ObjectBuildException {
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new ObjectBuildException("ClassNotFoundException when build by " + className);
        }
        if (!clazz.isAssignableFrom(cls)) {
            throw new ObjectBuildException("not Assignable From " + clazz.getName() + " when build by " + className);
        }
        Object o = null;
        try {
            o = cls.newInstance();
        }
        catch (InstantiationException e2) {
            throw new ObjectBuildException("InstantiationException when new instance by " + className);
        }
        catch (IllegalAccessException e3) {
            throw new ObjectBuildException("IllegalAccessException when new instance by " + className);
        }
        if (prop == null) {
            return (T)o;
        }
       if (o instanceof IConfigSaver) {
            ((IConfigSaver)o).setConfig(prop);
        }
        final String[] arr$;
        final String[] mains = arr$ = MapTools.getSubKeys(prop);
        for (final String mainId : arr$) {
            String mainV = prop.get(mainId);
            if (mainV == null) {
                mainV = "";
            }
            final Map.Entry<String, String> p = new AbstractMap.SimpleEntry<String, String>(mainId, mainV);
            if (!setByMethod(p, o, cls)) {
                if (!setByField(p, o, cls, prop)) {
                    if (p.getKey().indexOf(".") <= 0) {
                        ignoreProp("can't set properties " + p.getKey() + "=" + p.getValue() + "@ build " + clazz);
                    }
                }
            }
        }
        return (T)o;
    }
    
    private static void ignoreProp(final String s) {
        if (ObjectBuilder.ignorePropWarn) {
            System.out.println(s);
        }
    }
    
    private static boolean isFieldNameAndTypeEqual(final Field f, final String key, final String value) {
        final String nkey = StringUtils.uncapitalize(key);
        return f.getName().equals(nkey) && StringObjectUtil.canWrap2Class(value, f.getType());
    }
    
    private static boolean isFieldNameWithClassNameEqual(final Field f, final String key, final String value) {
        final String nkey = StringUtils.uncapitalize(key);
        if (!f.getName().equals(nkey)) {
            return false;
        }
        if (value == null || value.length() == 0) {
            return false;
        }
        final Class<?> c = f.getType();
        try {
            final Class<?> cs = Class.forName(value);
            if (c.isAssignableFrom(cs)) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    private static boolean isFieldWithCollectionList(final Field f, final String key, final String s) {
        final Class<?> cs = List.class;
        return cs.isAssignableFrom(f.getType()) && f.getName().equals(key) && s.equals("");
    }
    
    private static boolean isFieldWithCollectionMap(final Field f, final String key, final String s) {
        final Class<?> cs = Map.class;
        return cs.isAssignableFrom(f.getType()) && f.getName().equals(key) && s.equals("");
    }
    
    private static boolean isMethodNameAndTypeEqual(final Method m, final String key, final String value) {
        final String nkey = "set" + StringUtils.capitalize(key);
        if (!m.getName().equals(nkey)) {
            return false;
        }
        final Class<?>[] argtypes = m.getParameterTypes();
        return argtypes != null && argtypes.length == 1 && StringObjectUtil.canWrap2Class(value, argtypes[0]);
    }
    
    private static boolean setByField(final Map.Entry<String, String> p, final Object o, final Class<?> cls, final Map<String, String> props) throws ObjectBuildException {
        final Field[] arr$;
        final Field[] fs = arr$ = cls.getDeclaredFields();
        for (final Field f : arr$) {
            if (isFieldNameAndTypeEqual(f, p.getKey(), p.getValue())) {
                f.setAccessible(true);
                final Object obj = StringObjectUtil.wrapClass(p.getValue(), f.getType());
                try {
                    if (f.getType().isArray()) {
                        f.set(o, obj);
                    }
                    else {
                        f.set(o, obj);
                    }
                }
                catch (IllegalArgumentException e) {
                    throw new ObjectBuildException("IllegalArgumentException when set prop (byfield) " + p.getKey() + "=" + p.getValue() + "");
                }
                catch (IllegalAccessException e2) {
                    throw new ObjectBuildException("IllegalAccessException when set prop (byField)" + p.getKey() + "=" + p.getValue() + "");
                }
                return true;
            }
            if (isFieldNameWithClassNameEqual(f, p.getKey(), p.getValue()) && p.getValue() != null && p.getValue().length() > 0) {
                f.setAccessible(true);
                try {
                    final Object obj = build(f.getType(), p.getValue(), MapTools.getSub(props, f.getName()));
                    f.set(o, obj);
                    return true;
                }
                catch (IllegalArgumentException e3) {
                    throw new ObjectBuildException("IllegalArgumentException when set prop (byfield) " + p.getKey() + "=" + p.getValue() + "");
                }
                catch (IllegalAccessException e4) {
                    throw new ObjectBuildException("IllegalAccessException when set prop (byField)" + p.getKey() + "=" + p.getValue() + "");
                }
            }
            if (isFieldWithCollectionList(f, p.getKey(), p.getValue())) {
                final Map<String, String> msub = MapTools.getSub(props, StringUtils.substringBefore(p.getKey(), "."));
                final String[] arr$2;
                final String[] ss = arr$2 = MapTools.getSubKeys(msub);
                for (final String s : arr$2) {
                    final Map<String, String> msub2 = MapTools.getSub(msub, s);
                    try {
                        f.setAccessible(true);
                        final List l = (List)f.get(o);
                        Class clazzActual = Object.class;
                        if (f.getGenericType() instanceof ParameterizedType) {
                            final ParameterizedType pt = (ParameterizedType)f.getGenericType();
                            clazzActual = (Class)pt.getActualTypeArguments()[0];
                        }
                        Object listobj = null;
                        if (!clazzActual.equals(String.class)) {
                            listobj = build((Class<Object>)clazzActual, msub.get(s), msub2);
                        }
                        else {
                            listobj = msub.get(s);
                        }
                        l.add(listobj);
                    }
                    catch (IllegalArgumentException e5) {
                        throw new ObjectBuildException("IllegalArgumentException when set prop (byfield List) " + p.getKey() + "=" + p.getValue() + "");
                    }
                    catch (IllegalAccessException e6) {
                        throw new ObjectBuildException("IllegalAccessException when set prop (byfield List) " + p.getKey() + "=" + p.getValue() + "");
                    }
                }
                return true;
            }
            if (isFieldWithCollectionMap(f, p.getKey(), p.getValue())) {
                final Map<String, String> msub = MapTools.getSub(props, StringUtils.substringBefore(p.getKey(), "."));
                final String[] arr$2;
                final String[] ss = arr$2 = MapTools.getSubKeys(msub);
                for (final String s : arr$2) {
                    final Map<String, String> msub2 = MapTools.getSub(msub, s);
                    try {
                        f.setAccessible(true);
                        final Map i = (Map)f.get(o);
                        Class clazzActual = Object.class;
                        if (f.getGenericType() instanceof ParameterizedType) {
                            final ParameterizedType pt = (ParameterizedType)f.getGenericType();
                            if (pt.getActualTypeArguments().length != 2 || pt.getActualTypeArguments()[0] != String.class) {
                                throw new ObjectBuildException("not support Map GenericType when set prop (byfield Map) " + p.getKey() + "=" + p.getValue() + "");
                            }
                            clazzActual = (Class)pt.getActualTypeArguments()[1];
                        }
                        Object listobj = null;
                        if (!clazzActual.equals(String.class)) {
                            listobj = build((Class<Object>)clazzActual, msub.get(s), msub2);
                        }
                        else {
                            listobj = msub.get(s);
                        }
                        i.put(s, listobj);
                    }
                    catch (IllegalArgumentException e5) {
                        throw new ObjectBuildException("IllegalArgumentException when set prop (byfield Map) " + p.getKey() + "=" + p.getValue() + "");
                    }
                    catch (IllegalAccessException e6) {
                        throw new ObjectBuildException("IllegalAccessException when set prop (byfield Map) " + p.getKey() + "=" + p.getValue() + "");
                    }
                }
                return true;
            }
        }
        return cls.getSuperclass() != Object.class && setByField(p, o, cls.getSuperclass(), props);
    }
    
    private static boolean setByMethod(final Map.Entry<String, String> p, final Object o, final Class<?> cls) throws ObjectBuildException {
        final Method[] arr$;
        final Method[] ms = arr$ = cls.getDeclaredMethods();
        for (final Method m : arr$) {
            if (isMethodNameAndTypeEqual(m, p.getKey(), p.getValue())) {
                m.setAccessible(true);
                final Object arg = StringObjectUtil.wrapClass(p.getValue(), m.getParameterTypes()[0]);
                try {
                    m.invoke(o, arg);
                }
                catch (IllegalArgumentException e) {
                    throw new ObjectBuildException("IllegalArgumentException when set prop " + p.getKey() + "=" + p.getValue() + "");
                }
                catch (IllegalAccessException e2) {
                    throw new ObjectBuildException("IllegalAccessException when set prop " + p.getKey() + "=" + p.getValue() + "");
                }
                catch (InvocationTargetException e3) {
                    throw new ObjectBuildException("InvocationTargetException when set prop " + p.getKey() + "=" + p.getValue() + "");
                }
                return true;
            }
        }
        return false;
    }
    
    static {
        ObjectBuilder.ignorePropWarn = false;
        final String v = System.getenv("AEE_OBJECTBUILDER_DEBUG");
        if (v != null && v.equals("TRUE")) {
            ObjectBuilder.ignorePropWarn = true;
        }
    }
}
