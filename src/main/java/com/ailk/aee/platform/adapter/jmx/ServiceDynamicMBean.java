// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.adapter.jmx;

import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import java.util.HashMap;
import java.util.Map;
import javax.management.AttributeList;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import java.lang.reflect.Constructor;
import com.ailk.aee.platform.annotation.MethodOption;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanParameterInfo;
import com.ailk.aee.platform.annotation.MethodOptions;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.ArrayList;
import com.ailk.aee.platform.service.LoggerManagerService;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import javax.management.MBeanInfo;
import com.ailk.aee.platform.service.AbstractPlatformService;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import javax.management.DynamicMBean;

@CVSID("$Id: ServiceDynamicMBean.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ServiceDynamicMBean implements DynamicMBean
{
    private AbstractPlatformService infoProvider;
    private MBeanInfo mBeanInfo;
    private Field[] infoProviderFields;
    private Method[] infoProviderMethods;
    private MBeanAttributeInfo[] attributes;
    private MBeanConstructorInfo[] constructors;
    private MBeanOperationInfo[] operations;
    
    public static void main(final String[] aa) throws Exception {
        final ObjectName objectName = new ObjectName("test:name=logger");
        ManagementFactory.getPlatformMBeanServer().registerMBean(new ServiceDynamicMBean(new LoggerManagerService()), objectName);
        while (true) {
            Thread.sleep(30000L);
        }
    }
    
    public ServiceDynamicMBean(final AbstractPlatformService provider) {
        this.infoProvider = null;
        this.mBeanInfo = null;
        this.infoProviderFields = null;
        this.infoProviderMethods = null;
        this.attributes = null;
        this.constructors = new MBeanConstructorInfo[1];
        this.operations = null;
        this.infoProvider = provider;
        final Class<?> providerClz = this.infoProvider.getClass();
        this.infoProviderMethods = providerClz.getDeclaredMethods();
        if (this.infoProviderMethods != null && this.infoProviderMethods.length > 0) {
            final ArrayList<MBeanOperationInfo> operationList = new ArrayList<MBeanOperationInfo>();
            for (int i = 0; i < this.infoProviderMethods.length; ++i) {
                final Method tempMethod = this.infoProviderMethods[i];
                if (tempMethod.getAnnotation(PlatformServiceMethod.class) != null) {
                    if (tempMethod.getAnnotation(MethodOptions.class) != null) {
                        final MethodOption[] options = tempMethod.getAnnotation(MethodOptions.class).options();
                        final MBeanParameterInfo[] paramsInfo = new MBeanParameterInfo[options.length];
                        System.out.println(paramsInfo.length);
                        for (int m = 0; m < options.length; ++m) {
                            System.out.println("----" + options[m].name());
                            paramsInfo[m] = new MBeanParameterInfo(options[m].name(), String.class.getCanonicalName(), options[m].description());
                        }
                        System.out.println(tempMethod.getName());
                        final MBeanOperationInfo operationInfo = new MBeanOperationInfo(tempMethod.getName(), tempMethod.getName(), paramsInfo, tempMethod.getReturnType().getName(), 3);
                        operationList.add(operationInfo);
                    }
                }
            }
            this.operations = operationList.toArray(new MBeanOperationInfo[0]);
        }
        else {
            this.operations = new MBeanOperationInfo[0];
        }
        System.out.println(this.operations.length);
        this.infoProviderFields = providerClz.getDeclaredFields();
        if (this.infoProviderFields != null && this.infoProviderFields.length > 0) {
            final ArrayList<MBeanAttributeInfo> attributeList = new ArrayList<MBeanAttributeInfo>();
            for (int i = 0; i < this.infoProviderFields.length; ++i) {
                final Field tempField = this.infoProviderFields[i];
                try {
                    attributeList.add(new MBeanAttributeInfo(tempField.getName(), tempField.getType().toString(), tempField.getName(), false, false, false));
                }
                catch (Exception e) {}
            }
            this.attributes = attributeList.toArray(new MBeanAttributeInfo[0]);
        }
        else {
            this.attributes = new MBeanAttributeInfo[0];
        }
        final Constructor<?>[] providerConstructors = this.infoProvider.getClass().getConstructors();
        this.constructors[0] = new MBeanConstructorInfo("Constructs a InfoProvider object", providerConstructors[0]);
        this.mBeanInfo = new MBeanInfo(this.infoProvider.getClass().getCanonicalName(), this.infoProvider.getClass().getCanonicalName(), this.attributes, this.constructors, this.operations, new MBeanNotificationInfo[0]);
    }
    
    @Override
    public Object getAttribute(final String arg0) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return null;
    }
    
    @Override
    public AttributeList getAttributes(final String[] arg0) {
        return null;
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }
    
    @Override
    public Object invoke(final String operationName, final Object[] arg1, final String[] arg2) throws MBeanException, ReflectionException {
        for (final Method tempMethod : this.infoProviderMethods) {
            if (tempMethod.getName().equals(operationName)) {
                try {
                    final Method method = this.infoProvider.getClass().getDeclaredMethod(operationName, Map.class);
                    final MethodOption[] options = method.getAnnotation(MethodOptions.class).options();
                    final Map<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < options.length; ++i) {
                        map.put(options[i].name(), (arg1[i] == null || arg1[i].toString().length() == 0) ? options[i].defValue() : arg1[i].toString());
                    }
                    return method.invoke(this.infoProvider, map);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    @Override
    public void setAttribute(final Attribute arg0) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList arg0) {
        return null;
    }
}
