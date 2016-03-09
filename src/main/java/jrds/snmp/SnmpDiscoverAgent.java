package jrds.snmp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;
import jrds.webapp.Discover.ProbeDescSummary;
import jrds.webapp.DiscoverAgent;

import org.apache.logging.log4j.Level;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpDiscoverAgent extends DiscoverAgent {
    //Used to check if snmp is on
    static private final OID sysObjectID = new OID("1.3.6.1.2.1.1.2.0");

    static private class LocalSnmpConnection extends SnmpConnection {
        Snmp snmp;
        Target target;
        @Override
        public final boolean start() {
            try {
                snmp = new Snmp(new DefaultUdpTransportMapping());
                snmp.listen();
            } catch (IOException e) {
            }
            return true;
        }
        @Override
        public final void stop() {
            try {
                snmp.close();
            } catch (IOException e) {
            }
        }
        @Override
        public final Snmp getSnmp() {
            return snmp;
        }

        @Override
        public final boolean isStarted() {
            return true;
        }
        /* (non-Javadoc)
         * @see jrds.snmp.SnmpConnection#getConnection()
         */
        @Override
        public Target getConnection() {
            return target;
        }
    }

    private Target hosttarget;
    private LocalSnmpConnection active;
    //Sort descriptions
    private final LinkedList<String> sortedProbeName = new LinkedList<String>();
    private final Map<String, ProbeDescSummary> summaries = new HashMap<String, ProbeDescSummary>();

    public SnmpDiscoverAgent() {
        super("SNMP", jrds.probe.snmp.SnmpProbe.class);
    }

    private Target makeSnmpTarget(HttpServletRequest request) throws UnknownHostException{
        String hostname = request.getParameter("host");
        String community = request.getParameter("discoverSnmpCommunity");
        if(community == null) {
            community = "public";
        }
        int port = jrds.Util.parseStringNumber(request.getParameter("discoverSnmpPort"), 161);
        IpAddress addr = new UdpAddress(InetAddress.getByName(hostname), port);
        Target hosttarget = new CommunityTarget(addr, new OctetString(community));
        hosttarget.setVersion(SnmpConstants.version2c);
        return hosttarget;
    }

    @Override
    public void discoverPost(String hostname, JrdsElement hostEleme,
            Map<String, JrdsDocument> probdescs, HttpServletRequest request) {

        boolean withOid = false;
        String withOidStr = request.getParameter("discoverWithOid");
        if(withOidStr != null && "true".equals(withOidStr.toLowerCase()))
            withOid = true;

        Set<String> done = new HashSet<String>();

        log(Level.DEBUG, "Will search for probes %s", sortedProbeName);
        for(String name: sortedProbeName) {
            ProbeDescSummary summary = summaries.get(name);
            if(summary == null) {
                log(Level.ERROR, "ProbeDesc not valid for %s, skip it", name);
                done.add(name);
                continue;
            }
            if(done.contains(name)) {
                log(Level.TRACE, "ProbeDesc %s already done, it must be hidden", name);                
                continue;
            }
            log(Level.TRACE, "Trying to discover probe %s", name);

            try {
                boolean found = false;
                if(summary.isIndexed ) {
                    found = (enumerateIndexed(hostEleme, summary, withOid) > 0);
                }
                else {
                    found = doesExist(hostEleme, summary);
                }
                if(found) {
                    log(Level.DEBUG, "%s found", name);
                    done.add(name);
                    //Add all the hidden probes desc to the already done one
                    String hidesStr = summary.specifics.get("hides");
                    if(hidesStr != null && ! hidesStr.isEmpty()) {
                        for(String hides: hidesStr.split(",")) {
                            log(Level.DEBUG, "%s hides %s", name, hides.trim());
                            done.add(hides.trim());
                        }
                    }
                }
            } catch (Exception e1) {
                log(Level.ERROR, e1, "Error detecting %s: %s" , name, e1);
            }
        }
        active.doStop();
    }

    private boolean doesExist(JrdsElement hostEleme, ProbeDescSummary summary) throws IOException {
        OID OidExist = new OID(summary.specifics.get("existOid"));
        Map<OID, Object> found = SnmpRequester.RAW.doSnmpGet(active, Collections.singletonList(OidExist));
        if(found.size() > 0) {
            addProbe(hostEleme, summary.name, null, null, null, null);
            log(Level.TRACE, "%s does exist: %s", summary.name, found.values());
            return true;
        }
        else {
            return false;
        }
    }

    private String getLabel(LocalSnmpConnection active, OID labelOID) throws IOException {
        Map<OID, Object> rowLabel = SnmpRequester.RAW.doSnmpGet(active, Collections.singletonList(labelOID));
        for(Map.Entry<OID, Object> labelEntry: rowLabel.entrySet()) {
            String label = labelEntry.getValue().toString();
            if(label.length() >= 1)
                return label;
        }
        return null;
    }

    private int enumerateIndexed(JrdsElement hostEleme, ProbeDescSummary summary, boolean withOid) throws IOException {
        OID indexOid = new OID(summary.specifics.get("indexOid"));
        int count = 0;
        log(Level.TRACE, "Will enumerate %s", indexOid);
        Set<OID> oidsSet = Collections.singleton(indexOid);
        Map<OID, Object> indexes = SnmpRequester.TREE.doSnmpGet(active, oidsSet);
        log(Level.TRACE, "Elements : %s", indexes);
        for(Map.Entry<OID, Object> e: indexes.entrySet()) {
            count++;
            Map<String, String> beans = new HashMap<String, String>(2);
            OID rowOid = e.getKey();
            String indexName = e.getValue().toString();
            beans.put("index", indexName);


            int[] index = Arrays.copyOfRange(rowOid.getValue(), indexOid.size(), rowOid.size());

            //If we wanted to generate a static oid
            if(withOid) {
                OID suffixOid = new OID(index);
                beans.put("oid", suffixOid.toString());
            }

            //We try to auto-generate the label
            String label = summary.specifics.get("labelOid");
            String labelValue = null;
            if (label != null && ! label.isEmpty()) {
                OID suffixOid = new OID(index);
                for(String lookin: label.split(",")) {
                    OID labelOID = new OID(lookin.trim() + "." + suffixOid.toString());
                    labelValue = getLabel(active, labelOID);
                    if (labelValue != null)
                        break;
                }
            }

            addProbe(hostEleme, summary.name, labelValue, null, null, beans);

        }
        return count;
    }

    @Override
    public List<FieldInfo> getFields() {
        FieldInfo community = new FieldInfo();
        community.dojoType = DojoType.TextBox;
        community.id = "discoverSnmpCommunity";
        community.label = "SNMP community";
        community.value = "public";

        FieldInfo port = new FieldInfo();
        port.dojoType = DojoType.TextBox;
        port.id = "discoverSnmpPort";
        port.label = "SNMP Port";
        port.value = "161";

        FieldInfo keepOID = new FieldInfo();
        keepOID.dojoType = DojoType.ToggleButton;
        keepOID.id = "discoverWithOid";
        keepOID.label = " Keep index OID ";
        keepOID.value = "false";

        return Arrays.asList(community, port, keepOID);
    }

    @Override
    public boolean exist(String hostname, HttpServletRequest request) {
        try {
            hosttarget = makeSnmpTarget(request);
            active = new LocalSnmpConnection();
            active.target = hosttarget;
            active.doStart();
            if(SnmpRequester.RAW.doSnmpGet(active, Collections.singleton(sysObjectID)).size() < 0) {
                log(Level.INFO, "SNMP not active on host %s", hostname);
                return false;
            }
            return true;
        } catch (UnknownHostException e) {
            log(Level.INFO, "Host name %s unknown", hostname);
            return false;
        } catch (IOException e1) {
            log(Level.INFO, "SNMP not active on host %s", hostname);
            return false;
        }
    }

    @Override
    public void addConnection(JrdsElement hostElement,
            HttpServletRequest request) {
        JrdsElement snmpElem = hostElement.addElement("connection", "type=jrds.snmp.SnmpConnection");
        if(hosttarget instanceof CommunityTarget) {
            CommunityTarget ct = (CommunityTarget) hosttarget;
            snmpElem.addElement("attr", "name=community").setTextContent(ct.getCommunity().toString());
        }
        snmpElem.addElement("attr", "name=version").setTextContent(Integer.toString( 1 + hosttarget.getVersion()));
    }

    @Override
    public boolean isGoodProbeDesc(ProbeDescSummary summary) {
        String index =  summary.specifics.get("indexOid");
        //drop indexed probes without index oid
        if(summary.isIndexed && (index == null || index.isEmpty()))
            return false;

        String doesExistOid = summary.specifics.get("existOid");
        //drop indexed probes without OID to check presence
        if(!summary.isIndexed && (doesExistOid == null || doesExistOid.isEmpty()))
            return false;

        return true;
    }

    @Override
    public void addProbe(JrdsElement hostElement, ProbeDescSummary summary,
            HttpServletRequest request) {
        String name = summary.name;

        //Don't discover if asked to don't do
        if(summary.specifics.get("nodiscover") != null)
            return;

        summaries.put(summary.name, summary);
        String hidesStr = summary.specifics.get("hides");
        if(hidesStr != null && ! hidesStr.trim().isEmpty()) {
            int pos = Integer.MAX_VALUE;
            for(String hides: hidesStr.split(",")) {
                int hidesPos = sortedProbeName.indexOf(hides.trim());
                pos = hidesPos != -1 ? Math.min(pos, hidesPos): pos;
            }
            if(pos > sortedProbeName.size()) {
                // No hides found, add at the end
                sortedProbeName.add(name);
            }
            else {
                // add before the first hidden probe
                sortedProbeName.add(pos, name);
            }
        }
        //No hide, just put
        else {
            sortedProbeName.add(name);
        }
        return;
    }

}
