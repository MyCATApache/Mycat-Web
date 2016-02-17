package jrds.probe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import jrds.ConnectedProbe;
import jrds.ProbeConnected;
import jrds.ProbeDesc;

import org.apache.logging.log4j.Level;

/**
 * 
 * @author Fabrice Bacchella 
 * @version $Revision: 407 $,  $Date: 2007-02-22 18:48:03 +0100 (jeu., 22 févr. 2007) $
 */
public class JMX extends ProbeConnected<String, Double, JMXConnection> implements ConnectedProbe {
    private Map<String, String> collectKeys = null;

    public JMX() {
        super(JMXConnection.class.getName());
    }

    @Override
    public Boolean configure() {
        collectKeys = new HashMap<String, String>();
        for(Map.Entry<String, String> e:getPd().getCollectStrings().entrySet()) {
            String dsName = e.getValue();
            String solved = jrds.Util.parseTemplate(e.getKey(), this);
            collectKeys.put(solved, dsName);
        }
        return super.configure();
    }

    @Override
    public Map<String, Double> getNewSampleValuesConnected(JMXConnection cnx) {
        MBeanServerConnection mbean =  cnx.getConnection();
        try {
            Set<String> collectKeys = getCollectMapping().keySet();
            Map<String, Double> retValues = new HashMap<String, Double>(collectKeys.size());

            log(Level.DEBUG, "will collect: %s", collectKeys);
            for(String collect: collectKeys) {
                int attrSplit = collect.indexOf(':');
                attrSplit = collect.indexOf('/', attrSplit);
                ObjectName mbeanName = new ObjectName(collect.substring(0, attrSplit));
                String[] jmxPath = collect.substring(attrSplit+1).split("/");
                String attributeName =  jmxPath[0];
                log(Level.TRACE, "mbean name= %s, attributeName = %s", mbeanName, attributeName);                 
                try {
                    Object attr = mbean.getAttribute(mbeanName, attributeName);

                    Number v = resolvJmxObject(attr, jmxPath);
                    log(Level.TRACE, "JMX Path: %s = %s", collect, v);
                    retValues.put(collect, v.doubleValue());
                } catch (AttributeNotFoundException e1) {
                    log(Level.ERROR, e1, "Invalide JMX attribue %s", attributeName);
                } catch (InstanceNotFoundException e1) {
                	e1.printStackTrace();
                    log(Level.ERROR, e1, "JMX instance not found: %s", e1.getMessage());
                } catch (MBeanException e1) {
                    log(Level.ERROR, e1, "JMX MBeanException: %s", e1);
                } catch (ReflectionException e1) {
                    log(Level.ERROR, e1, "JMX reflection error: %s", e1);
                } catch (IOException e1) {
                    log(Level.ERROR, e1, "JMX IO error: %s", e1);
                }
            }
            return retValues;
        } catch (MalformedObjectNameException e) {
            log(Level.ERROR, e, "JMX name error: %s", e);
        } catch (NullPointerException e) {
            log(Level.ERROR, e, "JMX error: %s", e);
        }

        return null;
    }

    @Override
    public String getSourceType() {
        return "JMX";
    }

    /**
     * Try to extract a numerical value from a jmx Path pointing to a jmx object
     * If the attribute (element 0) of the path is a :
     * - Set, array or TabularData, the size is used
     * - Map, the second element is the key to the value
     * - CompositeData, the second element is the key to the value
     * @param jmxPath
     * @param o
     * @return
     * @throws UnsupportedEncodingException 
     */
    Number resolvJmxObject(Object o, String[] jmxPath) throws UnsupportedEncodingException {
        Object value = null;
        //Fast simple case
        if(o instanceof Number)
            return (Number) o;
        else if(o instanceof CompositeData && jmxPath.length == 2) {
            String subKey = URLDecoder.decode(jmxPath[1], "UTF-8");
            CompositeData co = (CompositeData) o;
            value = co.get(subKey);
        }
        else if(o instanceof Map<?, ?> && jmxPath.length == 2) {
            String subKey = URLDecoder.decode(jmxPath[1], "UTF-8");
            value = ((Map<?, ?>) o).get(subKey);
        }
        else if(o instanceof Collection<?>) {
            return ((Collection<?>) o ).size();
        }
        else if(o instanceof TabularData) {
            return ((TabularData) o).size();
        }
        else if(o.getClass().isArray()) {
            return Array.getLength(o);
        }
        //Last try, make a wild guess
        else {
            value = o;
        }
        if(value instanceof Number) {
            return ((Number) value);
        }
        else if(value instanceof String) {
            return jrds.Util.parseStringNumber((String) value, Double.NaN);
        }
        return Double.NaN;
    }

    /* (non-Javadoc)
     * @see jrds.Probe#setPd(jrds.ProbeDesc)
     */
    @Override
    public void setPd(ProbeDesc pd) {
        super.setPd(pd);
        collectKeys = getPd().getCollectStrings();
    }

    /* (non-Javadoc)
     * @see jrds.Probe#getCollectkeys()
     */
    @Override
    public Map<String, String> getCollectMapping() {
        return collectKeys;
    }
}
