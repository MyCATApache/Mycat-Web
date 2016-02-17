package jrds.configuration;

import java.lang.reflect.InvocationTargetException;

import jrds.Filter;
import jrds.FilterXml;
import jrds.Util;
import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilterBuilder extends ConfigObjectBuilder<Filter> {

    static final private Logger logger = LogManager.getLogger(FilterBuilder.class);

    public FilterBuilder() {
        super(ConfigType.FILTER);
    }

    @Override
    Filter build(JrdsDocument n) throws InvocationTargetException {
        try {
            return makeFilter(n);
        } catch (SecurityException e) {
            throw new InvocationTargetException(e, FilterBuilder.class.getName());
        } catch (IllegalArgumentException e) {
            throw new InvocationTargetException(e, FilterBuilder.class.getName());
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, FilterBuilder.class.getName());
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, FilterBuilder.class.getName());
        } catch (InstantiationException e) {
            throw new InvocationTargetException(e, FilterBuilder.class.getName());
        }
    }

    public Filter makeFilter(JrdsDocument n) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        JrdsElement root = n.getRootElement();
        JrdsElement name = root.getElementbyName("name");
        if(name == null)
            return null;
        FilterXml f = new FilterXml(name.getTextContent());
        setMethod(root.getChildElementsByName("path"),f, "addPath", String.class);
        setMethod(root.getChildElementsByName("tag"),f, "addTag", String.class);
        setMethod(root.getChildElementsByName("qualifiedname"), f, "addGraph", String.class);
        doACL(f, n, root);
        logger.trace(Util.delayedFormatString("Filter loaded: %s", f.getName()));
        return f;
    }

}
