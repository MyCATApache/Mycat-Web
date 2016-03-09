package jrds.standalone;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;

import jrds.GraphDesc;
import jrds.ProbeDesc;
import jrds.PropertiesManager;
import jrds.configuration.ConfigObjectFactory;
import jrds.factories.ProbeMeta;
import jrds.factories.xml.JrdsDocument;
import jrds.starter.Starter;
import jrds.starter.StarterNode;
import jrds.webapp.DiscoverAgent;

import org.apache.logging.log4j.Level;

public class CheckJar extends CommandStarterImpl {

    public void start(String[] args) throws Exception {
        PropertiesManager pm = new PropertiesManager();
        pm.importSystemProps();
        pm.update();
        pm.configdir = null;
        pm.strictparsing = true;
        pm.loglevel = Level.ERROR;

        System.getProperties().setProperty("java.awt.headless","true");

        System.out.println("Starting parsing descriptions");
        ConfigObjectFactory conf = new ConfigObjectFactory(pm);
        Map<String, GraphDesc> grapMap = conf.setGraphDescMap();

        Set<Class<? extends DiscoverAgent>> daList = new HashSet<Class<? extends DiscoverAgent>>();
        Set<Class<? extends Starter>> externalStarters = new HashSet<Class<? extends Starter>>();

        for(String jarfile: args) {
            System.out.println("checking " + jarfile);

            //pm.setProperty("libspath", (new File(jarfile).getCanonicalPath()));
            //pm.update();
            pm.libspath.clear();
            URL jarfileurl = new File(jarfile).toURI().toURL();
            pm.libspath.add(jarfileurl.toURI());
            pm.extensionClassLoader = new URLClassLoader(new URL[] { jarfileurl }, getClass().getClassLoader()) {
                /* (non-Javadoc)
                 * @see java.lang.Object#toString()
                 */
                @Override
                public String toString() {
                    return "JRDS' class loader";
                }
            };


            ConfigObjectFactory confjar = new ConfigObjectFactory(pm);

            Map<String, GraphDesc> grapMapjar = confjar.setGraphDescMap();
            for(ProbeDesc pd: confjar.setProbeDescMap().values()) {
                Class<?> pc = pd.getProbeClass();
                while(pc != null && pc != StarterNode.class) {
                    if(pc.isAnnotationPresent(ProbeMeta.class)) {
                        ProbeMeta meta = pc.getAnnotation(ProbeMeta.class);
                        daList.add(meta.discoverAgent());
                        externalStarters.add(meta.timerStarter());
                    }
                    pc = pc.getSuperclass();
                }
                for(String ds: pd.getDs()) {
                    if(ds.length() > 20) {
                        System.out.println(String.format("DS name %s too long for probe description %s", ds, pd.getName() ));
                    }
                }
                Collection<String> graphs = pd.getGraphClasses();
                if(graphs.size() == 0) {
                    System.out.println("no graphs for probe desc: " + pd.getName());
                    continue;
                }
                for(String graph: graphs) {
                    if(! grapMap.containsKey(graph) && ! grapMapjar.containsKey(graph)) {
                        System.out.println("Unknown graph " + graph + " for probe desc: " + pd.getName());
                    }
                }
            }
        }
        DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        JrdsDocument doc = new JrdsDocument(instance.newDocumentBuilder().newDocument());
        doc.doRootElement("div");
        for(Class<? extends DiscoverAgent> daClass: daList) {
            DiscoverAgent da = daClass.getConstructor().newInstance();
            da.doHtmlDiscoverFields(doc);
        }
        System.out.println("Discovery <div> will be:");
        Map<String, String> prop = new HashMap<String, String>(4);
        prop.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
        prop.put(OutputKeys.INDENT, "yes");
        prop.put(OutputKeys.STANDALONE, "yes");
        prop.put("{http://xml.apache.org/xslt}indent-amount", "4");
        jrds.Util.serialize(doc, System.out, null, prop);
        System.out.println(externalStarters);
    }
}
