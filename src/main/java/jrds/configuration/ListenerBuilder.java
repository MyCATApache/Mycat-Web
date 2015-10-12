package jrds.configuration;

import java.lang.reflect.InvocationTargetException;

import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;
import jrds.starter.Listener;

@SuppressWarnings("rawtypes")
public class ListenerBuilder extends ConfigObjectBuilder<Listener<?,?>> {

    private ClassLoader classLoader = ListenerBuilder.class.getClassLoader();

    protected ListenerBuilder() {
        super(ConfigType.LISTENER);
    }

    @Override
    Listener build(JrdsDocument n) throws InvocationTargetException {
        JrdsElement root = n.getRootElement();
        String className = root.getAttribute("class");
        if(className != null)
            className = className.trim();
        if(className.isEmpty())
            return null;
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Listener> starterClass = (Class<? extends Listener>) classLoader.loadClass(className);
            Listener s = starterClass.newInstance();
            return s;
        } catch (Exception e) {
            throw new InvocationTargetException(e, ListenerBuilder.class.getName());
        }
    }

    /**
     * @param classLoader the classLoader to set
     */
    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
