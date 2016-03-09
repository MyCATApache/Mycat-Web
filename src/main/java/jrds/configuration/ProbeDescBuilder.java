package jrds.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

import jrds.GraphDesc;
import jrds.Probe;
import jrds.ProbeDesc;
import jrds.Util;
import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;

import org.apache.logging.log4j.*;

public class ProbeDescBuilder extends ConfigObjectBuilder<ProbeDesc> {
    static final private Logger logger = LogManager.getLogger(ProbeDescBuilder.class);

    private ClassLoader classLoader = ProbeDescBuilder.class.getClassLoader();
    private Map<String, GraphDesc> graphDescMap = Collections.emptyMap();

    public ProbeDescBuilder() {
        super(ConfigType.PROBEDESC);
    }

    @Override
    ProbeDesc build(JrdsDocument n) throws InvocationTargetException {
        try {
            return makeProbeDesc(n);
        } catch (SecurityException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (IllegalArgumentException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (InvocationTargetException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (ClassNotFoundException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (NoClassDefFoundError e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        } catch (InstantiationException e) {
            throw new InvocationTargetException(e, ProbeDescBuilder.class.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public ProbeDesc makeProbeDesc(JrdsDocument n) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        ProbeDesc pd = new ProbeDesc();

        JrdsElement root = n.getRootElement();
        setMethod(root.getElementbyName("probeName"), pd, "setProbeName");
        setMethod(root.getElementbyName("name"), pd, "setName");
        setMethod(root.getElementbyName("index"), pd, "setIndex");

        logger.trace(Util.delayedFormatString("Creating probe description %s", pd.getName()));

        JrdsElement classElem = root.getElementbyName("probeClass");
        if(classElem == null) {
            throw new RuntimeException("Probe " + pd.getProbeName() + "defined without class");
        }
        String className = classElem.getTextContent().trim();
        Class<? extends Probe<?,?>> c = (Class<? extends Probe<?,?>>) classLoader.loadClass(className);
        pd.setProbeClass(c);

        pd.setHeartBeatDefault(pm.step * 2);

        setMethod(root.getElementbyName("uptimefactor"), pd, "setUptimefactor", Float.TYPE);

        boolean withgraphs = false;
        JrdsElement graphsElement= root.getElementbyName("graphs");
        if(graphsElement != null) {
            for(JrdsElement e: graphsElement.getChildElementsByName("name")) {
                String graphName = e.getTextContent();
                graphName = graphName != null ? graphName.trim() : "";
                if(graphDescMap.containsKey(graphName)) {
                    pd.addGraph(graphName);
                    withgraphs = true;
                    logger.trace(Util.delayedFormatString("Adding graph: %s", graphName));
                }
                else {
                    logger.warn(Util.delayedFormatString("Unknown graph %s for probe %s", graphName, pd.getName()));
                }
            }
        }
        if(! withgraphs) {
            logger.warn(Util.delayedFormatString("No valid graph found for %s", pd.getName()));
        }

        for(JrdsElement specificNode: root.getChildElementsByName("specific")) {
            Map<String, String> m = specificNode.attrMap();
            if(m != null) {
                String name = m.get("name");
                String value = specificNode.getTextContent().trim();
                pd.addSpecific(name, value);
                logger.trace(Util.delayedFormatString("Specific added: %s='%s'", name, value));
            }
        }

        JrdsElement requesterElement = root.getElementbyName("snmpRequester");
        if(requesterElement != null) {
            String snmpRequester = requesterElement.getTextContent().trim();
            if(snmpRequester != null && ! "".equals(snmpRequester)) {
                pd.addSpecific("requester", snmpRequester);
                logger.trace(Util.delayedFormatString("Specific added: requester='%s'", snmpRequester));

            }
        }

        //Populating the default arguments map
        JrdsElement argsNode = root.getElementbyName("defaultargs");
        if(argsNode != null) {
            for(JrdsElement attr: argsNode.getChildElementsByName("attr")) {
                String beanName = attr.getAttribute("name");
                String beanValue = attr.getTextContent();
                pd.addDefaultArg(beanName, beanValue);
            }
        }

        for(Map<String, Object> dsMap: doDsList(pd.getName(), root)) {
            pd.add(dsMap);			
        }

        return pd;
    }

    /**
     * @param classLoader the classLoader to set
     */
    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setGraphDescMap(Map<String, GraphDesc> graphDescMap) {
        this.graphDescMap = graphDescMap;
    }

}
