package jrds.configuration;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import jrds.Filter;
import jrds.GraphDesc;
import jrds.HostInfo;
import jrds.Macro;
import jrds.ProbeDesc;
import jrds.Tab;
import jrds.factories.ProbeFactory;
import jrds.factories.xml.JrdsDocument;
import jrds.graphe.Sum;
import jrds.starter.Listener;
import jrds.starter.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigObjectFactory {
    static final private Logger logger = LogManager.getLogger(ConfigObjectFactory.class);

    private ProbeFactory pf;
    private ClassLoader cl = this.getClass().getClassLoader();
    private Map<String, GraphDesc> graphDescMap = Collections.emptyMap();
    private Map<String, Listener<?, ?>> listenerMap = Collections.emptyMap();
    Map<String, Macro> macrosmap = Collections.emptyMap();
    private final jrds.PropertiesManager pm;
    private Loader load = null;

    public ConfigObjectFactory(jrds.PropertiesManager pm){
        this.pm = pm;
        this.cl = pm.extensionClassLoader;
        init();
    }

    public ConfigObjectFactory(jrds.PropertiesManager pm, ClassLoader cl){
        this.pm = pm;
        this.cl = cl;
        init();
    }	

    private void init() {
        try {
            load = new Loader(pm.strictparsing);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Can't build loader parser", e);
        }

        logger.debug(jrds.Util.delayedFormatString("Scanning %s for probes libraries", pm.libspath));
        for(URI lib: pm.libspath) {
            logger.info(jrds.Util.delayedFormatString("Adding lib %s", lib));
            load.importUrl(lib);
        }

        if(pm.configdir !=null)
            load.importDir(pm.configdir);

        load.done();
    }

    public void addUrl(URI ressourceUrl) {
        load.importUrl(ressourceUrl);
    }

    public Map<String, JrdsDocument> getNodeMap(ConfigType ct) {
        return load.getRepository(ct);
    }

    public <BuildObject> Map<String, BuildObject> getObjectMap(ConfigObjectBuilder<BuildObject> ob, Map<String, JrdsDocument> nodeMap) {
        Map<String, BuildObject> objectMap = new HashMap<String, BuildObject>();

        for(Map.Entry<String, JrdsDocument> e: nodeMap.entrySet()) {
            JrdsDocument n = e.getValue();
            BuildObject o = null;
            String name = ob.ct.getName(n);
            try {
                o = ob.build(n);
                if(o != null && name != null) {
                    objectMap.put(name, o);
                }
            } catch (InvocationTargetException ex) {
                logger.error("Fatal error for object of type " + ob.ct + " and name " + name + ":" + ex.getCause());
            }
            //Remove DOM object as soon as it's not needed any more
            nodeMap.remove(e.getKey());
        }
        return objectMap;
    }

    public Map<String, Macro> setMacroMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.MACRODEF);
        macrosmap = getObjectMap(new MacroBuilder(), nodemap);
        logger.debug(jrds.Util.delayedFormatString("Macro configured: %s", macrosmap.keySet()));
        return macrosmap;
    }

    public Map<String, GraphDesc> setGrapMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.GRAPH);
        GraphDescBuilder ob = new GraphDescBuilder();
        ob.setPm(pm);
        Map<String, GraphDesc> graphsMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Graphs configured: %s", graphsMap.keySet()));
        return graphsMap;
    }

    public Map<String, GraphDesc> setGraphDescMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.GRAPHDESC);
        GraphDescBuilder ob = new GraphDescBuilder();
        ob.setPm(pm);
        graphDescMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Graph description configured: %s", graphDescMap.keySet()));
        return graphDescMap;
    }

    public Map<String, ProbeDesc> setProbeDescMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.PROBEDESC);
        ProbeDescBuilder ob = new ProbeDescBuilder();
        ob.setClassLoader(cl);
        ob.setPm(pm);
        ob.setGraphDescMap(graphDescMap);
        Map<String, ProbeDesc> probeDescMap = getObjectMap(ob, nodemap);
        pf = new ProbeFactory(probeDescMap, graphDescMap);
        logger.debug(jrds.Util.delayedFormatString("Probe description configured: %s", probeDescMap.keySet()));
        return probeDescMap;
    }

    public Map<String, HostInfo> setHostMap(Map<String, Timer> timers) {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.HOSTS);
        HostBuilder ob = new HostBuilder();
        ob.setClassLoader(cl);
        ob.setMacros(macrosmap);
        ob.setProbeFactory(pf);
        ob.setPm(pm);
        ob.setTimers(timers);
        ob.setListeners(listenerMap);
        Map<String, HostInfo> hostsMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Hosts configured: %s", hostsMap.keySet()));
        return hostsMap;
    }

    public Map<String, Filter> setFilterMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.FILTER);
        FilterBuilder ob = new FilterBuilder();
        ob.setPm(pm);
        Map<String, Filter> filtersMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Filters configured: %s", filtersMap.keySet()));
        return filtersMap;
    }

    public Map<String, Sum> setSumMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.SUM);
        SumBuilder ob = new SumBuilder();
        ob.setPm(pm);
        Map<String, Sum> sumpsMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Sums configured: %s", sumpsMap.keySet()));
        return sumpsMap;
    }

    public Map<String, Tab> setTabMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.TAB);
        TabBuilder ob = new TabBuilder();
        Map<String, Tab> tabsMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("Tabs configured: %s", tabsMap.keySet()));
        return tabsMap;
    }

    public Map<String, Listener<?,?>> setListenerMap() {
        Map<String, JrdsDocument> nodemap = load.getRepository(ConfigType.LISTENER);
        ListenerBuilder ob = new ListenerBuilder();
        ob.setClassLoader(cl);
        listenerMap = getObjectMap(ob, nodemap);
        logger.debug(jrds.Util.delayedFormatString("listener configured: %s", listenerMap.keySet()));
        return listenerMap;
    }

    /**
     * @return the loader
     */
    Loader getLoader() {
        return load;
    }

}
