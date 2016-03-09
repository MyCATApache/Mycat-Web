package jrds.probe;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jrds.ProbeDesc;
import jrds.Util;
import jrds.factories.ProbeMeta;
import jrds.starter.XmlProvider;

import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;

/**
 * This probe can be used to collect values read from an XML document extracted from a defined URL
 * <p>
 * The specific keywords it uses are
 * <ul>
 * <li>upTimePath – An xpath to the uptime</li>
 * <li>nouptime – No uptime is provided</li>
 * <li>startTimePath – when no uptime is provided, the xpath to the start time of the agent</li>
 * <li>currentTimePath – when no uptime is provided, the xpath to the current time of the agent</li>
 * <li>timePattern – when no uptime is provided, a pattern that will be used by SimpleDateFormat object to parse time</li>
 * </ul> 
 * This probe uses <a href="http://hc.apache.org/httpclient-3.x/">Apache's Commons HttpClient</a>.
 * @author Fabrice Bacchella 
 */
@ProbeMeta(
        topStarter=jrds.starter.XmlProvider.class
        )
public class HttpXml extends HCHttpProbe {

    private Set<String> xpaths = null;
    private Map<String, String> collectKeys = null;

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#configure(java.util.List)
     */
    public Boolean  configure(List<Object> args) {
        boolean done = super.configure(args);
        finishConfig(args);
        return done;
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#configure(java.lang.String, java.util.List)
     */
    public Boolean configure(String file, List<Object> args) {
        boolean done = super.configure(file, args);
        finishConfig(args);
        return done;
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#configure(java.lang.Integer, java.util.List)
     */
    public Boolean configure(Integer port, List<Object> args) {
        boolean done = super.configure(port, args);
        finishConfig(args);
        return done;
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#configure(java.net.URL, java.util.List)
     */
    public Boolean configure(URL url, List<Object> args) {
        boolean done = super.configure(url, args);
        finishConfig(args);
        return done;
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#configure(java.lang.Integer, java.lang.String, java.util.List)
     */
    public Boolean configure(Integer port, String file, List<Object> args) {
        boolean done = super.configure(port, file, args);
        finishConfig(args);
        return done;
    }

    private void finishConfig(List<Object> args) {
        log(Level.TRACE, "Configuring collect xpath with %s", args);
        xpaths = new HashSet<String>(getPd().getCollectStrings().size());
        collectKeys = new HashMap<String, String>(xpaths.size());
        for(Map.Entry<String, String> e:getPd().getCollectStrings().entrySet()) {
            String xpath = e.getKey();
            String dsName = e.getValue();
            String solved = Util.parseTemplate(String.format(xpath, args.toArray()), this, args);
            xpaths.add(solved);
            collectKeys.put(solved, dsName);
        }
        log(Level.TRACE, "collect xpath mapping %s", collectKeys);
    }

    /* (non-Javadoc)
     * @see jrds.Probe#setPd(jrds.ProbeDesc)
     */
    @Override
    public void setPd(ProbeDesc pd) {
        super.setPd(pd);
        xpaths = getPd().getCollectStrings().keySet();
        collectKeys = getPd().getCollectStrings();
    }

    /* (non-Javadoc)
     * @see jrds.Probe#getCollectkeys()
     */
    @Override
    public Map<String, String> getCollectMapping() {
        return collectKeys;
    }

    /**
     * A method that can be overriden to extract more values from the XML document 
     * @param d the XML document read from the URL
     * @param variables already parsed variables that can be overriden
     * @return a new set of variables, this implementation return variables without modifications
     */
    public Map<String, Number> dom2Map(Document d, Map<String, Number> variables) {
        return variables;
    }

    /**
     * Extract the the uptime from the XML document, reusing the XML provider utilites
     * @param xmlstarter
     * @param d
     * @return
     */
    protected long findUptime(XmlProvider xmlstarter, Document d) {
        String upTimePath = getPd().getSpecific("upTimePath");
        if(upTimePath != null) {
            return xmlstarter.findUptime(d, upTimePath);
        }
        else if(getPd().getSpecific("nouptime") != null) {
            return Long.MAX_VALUE;
        }
        else {
            String startTimePath = getPd().getSpecific("startTimePath");
            String currentTimePath = getPd().getSpecific("currentTimePath");
            String timePattern = getPd().getSpecific("timePattern");
            if(startTimePath != null && currentTimePath != null && timePattern !=null) {
                DateFormat df = new SimpleDateFormat(timePattern);
                return xmlstarter.findUptimeByDate(d, startTimePath, currentTimePath, df);
            }
            else {
                log(Level.ERROR, "No xpath for the uptime");
                return 0;
            }
        }
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#parseStream(java.io.InputStream)
     */
    @Override
    protected Map<String, Number> parseStream(InputStream stream) {
        XmlProvider xmlstarter  = find(XmlProvider.class);
        if(xmlstarter == null) {
            log(Level.ERROR, "XML Provider not found");
            return null;
        }

        Document d = xmlstarter.getDocument(stream);
        setUptime(findUptime(xmlstarter, d));
        Map<String, Number> vars = new HashMap<String, Number>();
        xmlstarter.fileFromXpaths(d, xpaths, vars);
        log(Level.TRACE, "%s", vars);
        vars = dom2Map(d, vars);
        log(Level.TRACE, "%s", vars);
        return vars; 
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#getSourceType()
     */
    @Override
    public String getSourceType() {
        return "HttpXml";
    }

}
