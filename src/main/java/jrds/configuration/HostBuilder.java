package jrds.configuration;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jrds.ConnectedProbe;
import jrds.HostInfo;
import jrds.Macro;
import jrds.Probe;
import jrds.ProbeDesc;
import jrds.Util;
import jrds.factories.ArgFactory;
import jrds.factories.ProbeFactory;
import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;
import jrds.factories.xml.JrdsNode;
import jrds.probe.PassiveProbe;
import jrds.starter.Connection;
import jrds.starter.ConnectionInfo;
import jrds.starter.HostStarter;
import jrds.starter.Listener;
import jrds.starter.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HostBuilder extends ConfigObjectBuilder<HostInfo> {
    static final private Logger logger = LogManager.getLogger(HostBuilder.class);

    private ClassLoader classLoader = null;
    private ProbeFactory pf;
    private Map<String, Macro> macrosMap;
    private Map<String, Timer> timers = Collections.emptyMap();
    private Map<String, Listener<?, ?>> listeners = Collections.emptyMap();

    public HostBuilder() {
        super(ConfigType.HOSTS);
    }

    @Override
    HostInfo build(JrdsDocument n) throws InvocationTargetException {
        try {
            return makeHost(n);
        } catch (SecurityException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        } catch (IllegalArgumentException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        } catch (InvocationTargetException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        } catch (ClassNotFoundException e) {
            throw new InvocationTargetException(e, HostBuilder.class.getName());
        }
    }

    public HostInfo makeHost(JrdsDocument n) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        JrdsElement hostNode = n.getRootElement();
        String hostName = hostNode.getAttribute("name");
        String dnsHostname = hostNode.getAttribute("dnsName");
        if(hostName == null) {
            return null;
        }

        HostInfo host = null;
        if(dnsHostname != null) {
            host = new HostInfo(hostName, dnsHostname);
        }
        else {
            host = new HostInfo(hostName);
        }
        host.setHostDir(new File(pm.rrddir, host.getName()));

        String hidden = hostNode.getAttribute("hidden");
        host.setHidden(hidden != null && Boolean.parseBoolean(hidden));

        Map<String, Set<String>> collections = new HashMap<String, Set<String>>();

        parseFragment(hostNode, host, collections, null);

        return host;
    }

    private void parseFragment(JrdsElement fragment, HostInfo host, Map<String, Set<String>> collections, Map<String, String> properties) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // Find the connection for this host
        // Will the registered latter, in the starter node, one for each timer
        for(ConnectionInfo cnx: makeConnexion(fragment, host)) {
            host.addConnection(cnx);
        }

        for(JrdsElement tagElem: fragment.getChildElementsByName("tag")) {
            try {
                logger.trace(Util.delayedFormatString("adding tag %s to %s", tagElem, host));
                setMethod(tagElem, host, "addTag");
            } catch (InstantiationException e) {
            }
        }

        for(JrdsElement collectionNode: fragment.getChildElementsByName("collection")) {
            String name = collectionNode.getAttribute("name");
            Set<String> set = new HashSet<String>();
            for(JrdsElement e: collectionNode.getChildElementsByName("element")) {
                set.add(e.getTextContent());
            }
            collections.put(name, set);
        }

        for(JrdsElement macroNode: fragment.getChildElementsByName("macro")) {
            String name = macroNode.getAttribute("name");
            Macro m = macrosMap.get(name);
            logger.trace(Util.delayedFormatString("Adding macro %s: %s", name, m));
            if(m != null) {
                Map<String, String> macroProps = makeProperties(macroNode);
                Map<String, String> newProps = new HashMap<String, String>((properties !=null ? properties.size():0) + macroProps.size());
                if(properties != null)
                    newProps.putAll(properties);
                newProps.putAll(macroProps);
                JrdsDocument hostdoc = (JrdsDocument) fragment.getOwnerDocument();
                //Make a copy of the document fragment
                JrdsNode newDf = JrdsNode.build(hostdoc.importNode(m.getDf(), true));
                JrdsElement macrodef = JrdsNode.build( newDf.getFirstChild());
                parseFragment(macrodef, host, collections, newProps);
            }
            else {
                logger.error("Unknown macro:" + name);
            }
        }

        for(JrdsElement forNode: fragment.getChildElementsByName("for")) {
            Map<String, String> forattr = forNode.attrMap();
            String iterprop = forattr.get("var");
            Collection<String> set = null;
            String name = forNode.attrMap().get("collection");
            if(name != null)
                set = collections.get(name);
            else if(forattr.containsKey("min") && forattr.containsKey("max") && forattr.containsKey("step")) {
                int min = Util.parseStringNumber(forattr.get("min"), Integer.MAX_VALUE);
                int max = Util.parseStringNumber(forattr.get("max"), Integer.MIN_VALUE);
                int step = Util.parseStringNumber(forattr.get("step"), Integer.MIN_VALUE);
                if( min > max || step <= 0) {
                    logger.error("invalid range from " + min + " to " + max + " with step " + step);
                    break;
                }
                set = new ArrayList<String>((max - min)/step + 1);
                for(int i=min; i <= max; i+= step) {
                    set.add(Integer.toString(i));
                }
            }

            if(set != null) {
                logger.trace(Util.delayedFormatString("for using %s", set));

                for(String i: set) {
                    Map<String, String> temp;
                    if(properties != null) {
                        temp = new HashMap<String, String>(properties.size() +1);
                        temp.putAll(properties);
                        temp.put(iterprop, i);
                    }
                    else {
                        temp = Collections.singletonMap(iterprop, i);
                    }
                    parseFragment(forNode, host, collections, temp);
                }
            }
            else {
                logger.error("Invalid host configuration, collection " + name + " not found");
            }
        }

        for(JrdsElement probeNode: fragment.getChildElements()) {
            if(! "probe".equals(probeNode.getNodeName()) && ! "rrd".equals(probeNode.getNodeName()) )
                continue;
            try {
                makeProbe(probeNode, host, properties);
            } catch (Exception e) {
                logger.error("Probe creation failed for host " + host.getName() + ": ");
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();  
                e.printStackTrace(new PrintStream(buffer));
                logger.error(buffer);
            }
        }
    }

    public Probe<?,?> makeProbe(JrdsElement probeNode, HostInfo host, Map<String, String> properties) throws InvocationTargetException {
        Probe<?,?> p = null;
        String type = probeNode.attrMap().get("type");

        List<Map<String, Object>> dsList = doDsList(type, probeNode.getElementbyName("dslist"));
        if(dsList.size() > 0) {
            logger.trace(Util.delayedFormatString("Data source replaced for %s/%s: %s", host, type, dsList));
            ProbeDesc oldpd = pf.getProbeDesc(type);
            try {
                ProbeDesc pd = (ProbeDesc) oldpd.clone();
                pd.replaceDs(dsList);
                p = pf.makeProbe(pd);
            } catch (CloneNotSupportedException e) {
            }
        }
        else {
            p = pf.makeProbe(type);
        }
        if(p == null)
            return null;

        p.readProperties(pm);

        String timerName = probeNode.getAttribute("timer");
        if(timerName == null)
            timerName = Timer.DEFAULTNAME;
        Timer timer = timers.get(timerName);
        if(timer == null) {
            logger.error("Invalid timer '" + timerName + "' for probe " + host.getName() + "/" + type);
            return null;
        }
        else {
            logger.trace(Util.delayedFormatString("probe %s/%s will use timer %s", host, type, timer));
        }
        p.setStep(timer.getStep());
        p.setTimeout(timer.getTimeout());

        //The label is set
        String label = probeNode.getAttribute("label");
        if(label != null && ! "".equals(label)) {
            logger.trace(Util.delayedFormatString("Adding label %s to %s", label, p));
            p.setLabel(jrds.Util.parseTemplate(label, host, properties));;
        }

        //The host is set
        HostStarter shost = timer.getHost(host);
        p.setHost(shost);

        ProbeDesc pd = p.getPd();
        List<Object> args = ArgFactory.makeArgs(probeNode, host, properties);
        //Prepare the probe with the default beans values
        Map<String, String> defaultBeans = pd.getDefaultArgs();
        if(defaultBeans != null) {
            for(Map.Entry<String, String> e: defaultBeans.entrySet()) {
                try {
                    String beanName = e.getKey();
                    String beanValue = e.getValue();
                    PropertyDescriptor bean = pd.getBeanMap().get(beanName);
                    Object value;
                    //If the last argument is a list, give it to the template parser
                    Object lastArgs = args.isEmpty() ? null : args.get(args.size() - 1);
                    if(lastArgs instanceof List) {
                        value = ArgFactory.ConstructFromString(bean.getPropertyType(), Util.parseTemplate(beanValue, host, lastArgs));
                    }
                    else {
                        value = ArgFactory.ConstructFromString(bean.getPropertyType(), jrds.Util.parseTemplate(beanValue, host));
                    }
                    logger.trace(jrds.Util.delayedFormatString("Adding bean %s=%s (%s) to default args", beanName, value, value.getClass()));
                    bean.getWriteMethod().invoke(p, value);
                } catch (Exception ex) {
                    throw new RuntimeException("Invalid default bean " + e.getKey(), ex);
                }
            }
        }

        //Resolve the beans
        try {
            setAttributes(probeNode, p, pd.getBeanMap(), host);
        } catch (IllegalArgumentException e) {
            logger.error(String.format("Can't configure %s for %s: %s", pd.getName(), host, e));
            return null;
        }

        if( !pf.configure(p, args)) {
            logger.error(p + " configuration failed");
            return null;
        }

        //A connected probe, register the needed connection
        //It can be defined within the node, referenced by it's name, or it's implied name
        if(p instanceof ConnectedProbe) {
            String connectionName = null;
            ConnectedProbe cp = (ConnectedProbe) p;
            //Register the connections defined within the probe
            for(ConnectionInfo ci: makeConnexion(probeNode, p)) {
                ci.register(p);
            }
            String connexionName = probeNode.getAttribute("connection");
            if(connexionName != null && ! "".equals(connexionName)) {
                logger.trace(Util.delayedFormatString("Adding connection %s to %s", connexionName, p));
                connectionName = jrds.Util.parseTemplate(connexionName, host);
                cp.setConnectionName(connectionName);
            }
            else {
                connectionName = cp.getConnectionName();
            }
            //If the connection is not already registred, try looking for it
            //And register it with the host
            if(p.find(connectionName) == null) {
                if(logger.isTraceEnabled())
                    logger.trace(Util.delayedFormatString("Looking for connection %s in %s", connectionName, host.getConnections()));
                ConnectionInfo ci = host.getConnection(connectionName);
                if(ci != null)
                    ci.register(shost);
                else {
                    logger.error(Util.delayedFormatString("Failed to find a connection %s for a probe %s", connectionName, cp));
                    return null;
                }
            }
        }

        //A passive probe, perhaps a specific listener is defined
        if(p instanceof PassiveProbe) {
            PassiveProbe<?> pp = (PassiveProbe<?>) p;
            String listenerName = probeNode.getAttribute("listener");
            if(listenerName != null && ! listenerName.trim().isEmpty()) {
                Listener<?, ?> l = listeners.get(listenerName);
                if(l != null) {
                    pp.setListener(l);
                }
                else {
                    logger.error(Util.delayedFormatString("Listener name not found for %s: %s", pp, listenerName));
                }
            }
        }

        shost.addProbe(p);
        return p;
    }

    @SuppressWarnings("unchecked")
    /**
     * A compatibility method, snmp starter should be managed as a connection
     * @param node
     * @param p
     * @param host
     */
    private ConnectionInfo parseSnmp(JrdsElement node) {
        try {
            JrdsElement snmpNode = node.getElementbyName("snmp");
            if(snmpNode != null) {
                logger.info("found an old snmp starter, please update to a connection");
                String connectionClassName = "jrds.snmp.SnmpConnection";
                Class<? extends Connection<?>> connectionClass = (Class<? extends Connection<?>>) pm.extensionClassLoader.loadClass(connectionClassName);

                Map<String, String> attrs = new HashMap<String, String>();
                attrs.putAll(snmpNode.attrMap());
                return new ConnectionInfo(connectionClass, connectionClassName, Collections.emptyList(), attrs);
            }
        } catch (ClassNotFoundException e) {
            logger.debug("Class jrds.snmp.SnmpConnection not found");
        } catch (Exception e) {
            logger.error("Error creating SNMP connection: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Enumerate the connections found in an XML node
     * @param domNode a node to parse
     * @param host
     * @return
     */
    Set<ConnectionInfo> makeConnexion(JrdsElement domNode, Object parent) {
        Set<ConnectionInfo> connectionSet = new HashSet<ConnectionInfo>();

        //Check for the old SNMP connection node
        ConnectionInfo cnxSnmp = parseSnmp(domNode);
        if(cnxSnmp != null)
            connectionSet.add(cnxSnmp);

        for(JrdsElement cnxNode: domNode.getChildElementsByName("connection")) {
            String type = cnxNode.getAttribute("type");
            if(type == null) {
                logger.error("No type declared for a connection");
                continue;
            }
            String name = cnxNode.getAttribute("name");

            try {
                //Load the class for the connection
                @SuppressWarnings("unchecked")
                Class<? extends Connection<?>> connectionClass = (Class<? extends Connection<?>>) classLoader.loadClass(type);

                //Build the arguments vector for the connection
                List<Object> args = ArgFactory.makeArgs(cnxNode);

                //Resolve the bean for the connection
                Map<String, String> attrs = new HashMap<String, String>();
                for(JrdsElement attrNode: cnxNode.getChildElementsByName("attr")) {
                    String attrName = attrNode.getAttribute("name");
                    String textValue = Util.parseTemplate(attrNode.getTextContent(), parent);
                    attrs.put(attrName, textValue);
                }
                ConnectionInfo cnx = new ConnectionInfo(connectionClass, name, args, attrs);
                connectionSet.add(cnx);
                logger.debug(Util.delayedFormatString("Added connection %s to node %s", cnx, parent));
            }
            catch (NoClassDefFoundError ex) {
                logger.warn("Connection class not found: " + type+ ": " + ex);
            }
            catch (ClassCastException ex) {
                logger.warn(type + " is not a connection");
            }
            catch (LinkageError ex) {
                logger.warn("Incompatible code version during connection creation of type " + type +
                        ": " + ex, ex);
            }
            catch (Exception ex) {
                logger.warn("Error during connection creation of type " + type +
                        ": " + ex, ex);
            }
        }
        return connectionSet;
    }

    private void setAttributes(JrdsElement probeNode, Object o, Map<String, PropertyDescriptor> beans, Object... context) throws IllegalArgumentException, InvocationTargetException {
        //Resolve the beans
        for(JrdsElement attrNode: probeNode.getChildElementsByName("attr")) {
            String name = attrNode.getAttribute("name");
            PropertyDescriptor bean = beans.get(name);
            if(bean == null) {
                logger.error("Unknonw bean " + name);
                continue;
            }
            String textValue = Util.parseTemplate(attrNode.getTextContent(), context);
            logger.trace(Util.delayedFormatString("Fond attribute %s with value %s", name, textValue));
            try {
                Constructor<?> c = bean.getPropertyType().getConstructor(String.class);
                Object value = c.newInstance(textValue);
                bean.getWriteMethod().invoke(o, value);
            } catch (IllegalArgumentException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            } catch (SecurityException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            } catch (InstantiationException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            } catch (IllegalAccessException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            } catch (InvocationTargetException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, HostBuilder.class.getName());
            }
        }
    }

    private Map<String, String> makeProperties(JrdsElement n) {
        if(n == null)
            return Collections.emptyMap();
        JrdsElement propElem = n.getElementbyName("properties");
        if(propElem == null)
            return Collections.emptyMap();

        Map<String, String> props = new HashMap<String, String>();
        for(JrdsElement propNode: propElem.getChildElementsByName("entry")) {
            String key = propNode.getAttribute("key");
            if(key != null) {
                String value = propNode.getTextContent();
                logger.trace(Util.delayedFormatString("Adding propertie %s=%s", key, value));
                props.put(key, value);
            }
        }
        logger.debug(Util.delayedFormatString("Properties map: %s", props));
        return props;
    }

    /**
     * @param pf the pf to set
     */
    void setProbeFactory(ProbeFactory pf) {
        this.pf = pf;
    }

    /**
     * @param macrosMap the macrosMap to set
     */
    void setMacros(Map<String, Macro> macrosMap) {
        this.macrosMap = macrosMap;
    }

    /**
     * @param classLoader the classLoader to set
     */
    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * @param rootNode the rootNode to set
     */
    public void setTimers(Map<String, Timer> timers) {
        this.timers = timers;
    }

    public void setListeners(Map<String, Listener<?, ?>> listenerMap) {
        listeners = listenerMap;
    }

}
