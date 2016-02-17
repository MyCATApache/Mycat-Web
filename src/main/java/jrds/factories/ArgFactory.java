package jrds.factories;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jrds.Util;
import jrds.factories.xml.JrdsElement;

import org.apache.logging.log4j.*;

/**
 * A class to build args from a string constructor
 * @author Fabrice Bacchella 
 */

public final class ArgFactory {
    static private final Logger logger = LogManager.getLogger(ArgFactory.class);

    static private final String[] argPackages = new String[] {"java.lang.", "java.net.", "org.snmp4j.smi.", "java.io.", ""};
    static private final Map<String, Class<?>> classCache = new ConcurrentHashMap<String, Class<?>>();

    /**
     * This method build a list from an XML enumeration of element.
     * 
     * The enumeration is made of :<p/>
     * <code>&lt;arg type="type" value="value"></code><p/>
     * or<p/>
     * <code>&lt;arg type="type">value&lt;/value></code><p/>
     * This method is recursive, so it if finds some <code>list</code> elements instead of an <code>arg</code>, it will build a sub-list.
     * 
     * Unknown element will be silently ignored.
     * 
     * @param sequence an XML element that contains as sequence of <code>arg</code> or <code>list</code> elements.
     * @param arguments some object that will be used by a call to <code>jrds.Util.parseTemplate</code> for the arg values
     * @return
     * @throws InvocationTargetException 
     */
    public static final List<Object> makeArgs(JrdsElement sequence, Object... arguments) throws InvocationTargetException {
        List<JrdsElement> elements = sequence.getChildElements();
        List<Object> argsList = new ArrayList<Object>(elements.size());
        for(JrdsElement listNode: elements) {
            String localName = listNode.getNodeName();
            logger.trace(Util.delayedFormatString("Element to check: %s", localName));
            if("arg".equals(localName)) {
                String type = listNode.getAttribute("type");
                String value = null;
                if(listNode.hasAttribute("value"))
                    value = listNode.getAttribute("value");
                else
                    value = listNode.getTextContent();
                value = jrds.Util.parseTemplate(value, arguments);
                Object o = ArgFactory.ConstructFromString(resolvClass(type), value);
                argsList.add(o);                
            }
            else if("list".equals(localName)) {
                argsList.add(makeArgs(listNode, arguments));                
            }
        }
        logger.debug(Util.delayedFormatString("arg vector: %s", argsList));
        return argsList;
    }

    static final Class<?> resolvClass(String name) {
        if(classCache.containsKey(name))
            return classCache.get(name);
        Class<?> retValue = null;
        if("int".equals(name))
            return Integer.TYPE;
        else if("double".equals(name)) {
            return Double.TYPE;
        }
        else if("float".equals(name)) {
            return Float.TYPE;
        }
        else if("byte".equals(name)) {
            return Byte.TYPE;
        }
        else if("long".equals(name)) {
            return Long.TYPE;
        }
        else if("short".equals(name)) {
            return Short.TYPE;
        }
        else if("boolean".equals(name)) {
            return Boolean.TYPE;
        }
        else if("char".equals(name)) {
            return Character.TYPE;
        }
        for (String packageTry: argPackages) {
            try {
                retValue = Class.forName(packageTry + name);
            }
            catch (ClassNotFoundException ex) {
            }
            catch (NoClassDefFoundError ex) {
            }
        }
        if (retValue == null)
            throw new RuntimeException("Class " + name + " not found");
        classCache.put(name, retValue);
        return retValue;
    }

    /**
     * Create an object providing the class and a String argument. So the class must have
     * a constructor taking only a string as an argument.
     * 
     * It can manage native type and return an boxed object 
     * @param clazz
     * @param value
     * @return
     * @throws InvocationTargetException 
     */
    public static Object ConstructFromString(Class<?> clazz, String value) throws InvocationTargetException {
        try {
            Constructor<?> c = null;
            if(! clazz.isPrimitive() ) {
                c = clazz.getConstructor(String.class);
            }
            else if(clazz == Integer.TYPE) {
                c = Integer.class.getConstructor(String.class);
            }
            else if(clazz == Double.TYPE) {
                c = Double.class.getConstructor(String.class);
            }
            else if(clazz == Float.TYPE) {
                c = Float.class.getConstructor(String.class);
            }
            else if(clazz == Byte.TYPE) {
                c = Byte.class.getConstructor(String.class);
            }
            else if(clazz == Long.TYPE) {
                c = Long.class.getConstructor(String.class);
            }
            else if(clazz == Short.TYPE) {
                c = Short.class.getConstructor(String.class);
            }
            else if(clazz == Boolean.TYPE) {
                c = Boolean.class.getConstructor(String.class);
            }
            else if(clazz == Character.TYPE) {
                c = Character.class.getConstructor(String.class);
            }
            return c.newInstance(value);
        } catch (SecurityException e) {
            throw new InvocationTargetException(e, clazz.getName());
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, clazz.getName());
        } catch (IllegalArgumentException e) {
            throw new InvocationTargetException(e, clazz.getName());
        } catch (InstantiationException e) {
            throw new InvocationTargetException(e, clazz.getName());
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, clazz.getName());
        } catch (InvocationTargetException e) {
            throw new InvocationTargetException(e, clazz.getName());
        }
    }

    /**
     * Given an object, a bean name and a bean value, try to set the bean.
     * 
     * The bean type is expect to have a constructor taking a String argument
     * @param o the object to set
     * @param beanName the bean to set
     * @param beanValue the bean value
     * @throws InvocationTargetException
     */
    static public void beanSetter(Object o, String beanName, String beanValue) throws InvocationTargetException{
        try {
            PropertyDescriptor bean = new PropertyDescriptor(beanName, o.getClass());
            Method setMethod = bean.getWriteMethod();
            if(setMethod == null) {
                throw new InvocationTargetException(new NullPointerException(), String.format("Unknown bean %s", beanName));
            }
            Class<?> setArgType = bean.getPropertyType();
            Object argInstance = ArgFactory.ConstructFromString(setArgType, beanValue);
            setMethod.invoke(o, argInstance);       
        } catch (Exception e) {
            throw new InvocationTargetException(e, "invalid bean '" + beanName + "' for " + o);
        }
    }

    /**
     * Extract a map of the beans of an class. Only the beans listed in the ProbeBean class will be return
     * @param c a class to extract beans from
     * @return
     * @throws InvocationTargetException
     */
    static public Map<String, PropertyDescriptor> getBeanPropertiesMap(Class<?> c, Class<?> topClass) throws InvocationTargetException {
        Set<ProbeBean> beansAnnotations = ArgFactory.enumerateAnnotation(c, ProbeBean.class, topClass);
        if(beansAnnotations.isEmpty())
            return Collections.emptyMap();
        Map<String, PropertyDescriptor> beanProperties = new HashMap<String, PropertyDescriptor>();
        for(ProbeBean annotation: beansAnnotations) {
            for(String beanName: annotation.value()) {
                //Bean already found, don't work on it again
                if( beanProperties.containsKey(beanName)) {
                    continue;
                }
                try {
                    PropertyDescriptor bean = new PropertyDescriptor(beanName, c);
                    beanProperties.put(bean.getName(), bean);
                } catch (IntrospectionException e) {
                    throw new InvocationTargetException(e, "invalid bean " + beanName + " for " + c.getName());
                }

            }
        }
        return beanProperties;
    }

    /**
     * Enumerate the hierarchy of annotation for a class, until a certain class type is reached
     * @param searched the Class where the annotation is searched
     * @param annontationClass the annotation class
     * @param stop a class that will stop (included) the search 
     * @return
     */
    static public <T extends Annotation> Set<T> enumerateAnnotation(Class<?> searched, Class<T> annontationClass, Class<?> stop) {
        Set<T> annotations =  new LinkedHashSet<T>();
        while(searched != null && stop.isAssignableFrom(searched)) {
            if(searched.isAnnotationPresent(annontationClass)) {
                T annotation = searched.getAnnotation(annontationClass);
                annotations.add(annotation);
            }
            searched = searched.getSuperclass();
        }
        return annotations;
    }

}
