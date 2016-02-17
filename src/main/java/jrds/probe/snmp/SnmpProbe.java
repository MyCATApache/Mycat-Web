package jrds.probe.snmp;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jrds.ProbeConnected;
import jrds.factories.ProbeMeta;
import jrds.snmp.SnmpConnection;
import jrds.snmp.SnmpDiscoverAgent;
import jrds.snmp.SnmpRequester;

import org.apache.logging.log4j.Level;
import org.snmp4j.smi.OID;

/**
 * A abstract class from which all snmp probes should be derived.<p>
 * An usefull command to browse the content of an snmp agent :<p>
 * <quote>snmpbulkwalk -OX -c public -v 2c hostname  . | sed -e 's/\[.*\]//' -e 's/ =.*$//'|  grep '::' | uniq </quote>
 * @author bacchell
 * @param <SnmpConnection>
 */
@ProbeMeta(
        timerStarter=jrds.snmp.MainStarter.class,
        discoverAgent=SnmpDiscoverAgent.class
        )
public abstract class SnmpProbe extends ProbeConnected<OID, Object, SnmpConnection> {
    public final static String REQUESTERNAME = "requester";
    public final static String UPTIMEOIDNAME = "uptimeOid";
    private Map<OID, String> nameMap = null;
    private SnmpRequester requester;
    private int suffixLength = 1;
    private OID uptimeoid = null;

    public SnmpProbe() {
        super(SnmpConnection.class.getName());
    }

    /* (non-Javadoc)
     * @see jrds.Probe#readSpecific()
     */
    @Override
    public boolean readSpecific() {
        nameMap = getPd().getCollectOids();
        boolean readOK = false;
        try {
            String requesterName =  getPd().getSpecific(REQUESTERNAME);
            if(requesterName != null) {
                log(Level.TRACE, "Setting requester to %s", requesterName);
                requester = SnmpRequester.valueOf(requesterName.toUpperCase());
                readOK = true;
            }
            else {
                log(Level.ERROR, "No requester found");
            }
            String uptimeOidName =  getPd().getSpecific(UPTIMEOIDNAME);
            if(uptimeOidName != null) {
                log(Level.TRACE, "Setting uptime OID to %s", uptimeOidName);
                uptimeoid = new OID(uptimeOidName);
            }
        } catch (Exception e) {
            log(Level.ERROR, e, "Unable to read specific: %s", e.getMessage());
        }
        return readOK && super.readSpecific();
    }

    private Map<OID, String> initNameMap()
    {
        return getPd().getCollectOids();
    }

    public Map<OID, String> getOidNameMap()
    {
        if(nameMap == null)
            nameMap = initNameMap();
        return nameMap;
    }

    /**
     * Used to define the OID to collect
     * @return a set of OID to collect
     */
    protected abstract Set<OID> getOidSet();


    /* (non-Javadoc)
     * @see com.aol.jrds.Probe#getNewSampleValues()
     */
    @Override
    public Map<OID, Object> getNewSampleValuesConnected(SnmpConnection cnx) {
        Map<OID, Object> retValue = null;
        Collection<OID> oids = getOidSet();
        if(oids != null) {
            try {
                Map<OID, Object> rawValues = requester.doSnmpGet(cnx, oids);
                retValue = new HashMap<OID, Object>(rawValues.size());
                for(Map.Entry<OID, Object> e: rawValues.entrySet()) {
                    OID oid = new OID(e.getKey());
                    oid.trim(getSuffixLength());
                    retValue.put(oid, e.getValue());
                }
            } catch (IOException e) {
                log(Level.ERROR, e, "IO Error: %s", e.getMessage());
            }
        }

        return retValue;
    }

    /**
     * SnmpProbes can used either the default uptime from the standard MIBs or
     * use a specific one, defined using the uptimeOid specific
     * @see jrds.ProbeConnected#setUptime(jrds.starter.Connection)
     */
    @Override
    protected void setUptime(SnmpConnection cnx) {
        //If no uptimeoid, just use the snmp default
        if(uptimeoid == null)
            super.setUptime(cnx);
        else {
            cnx.readUptime(Collections.singleton(uptimeoid));
        }
    }

    /**
     * Prepare the SnmpVars to be stored by a probe. In the general case, for a snmp probe
     * the last element of the OID is removed.
     * If the value is a date, the value is the second since epoch
     * @param snmpVars
     * @return a Map of all the identified vars
     */
    @Override
    public Map<OID, Number> filterValues(Map<OID, Object>snmpVars) {
        Map<OID, Number> retValue = new HashMap<OID, Number>(snmpVars.size());
        for(Map.Entry<OID, Object> e: snmpVars.entrySet()) {
            OID oid = e.getKey();
            Object o = e.getValue();
            if( o instanceof Number) {
                retValue.put(oid, (Number)o);
            }
            if( o instanceof Date) {
                Date value = (Date) o;
                retValue.put(oid, new Double(value.getTime()));
            }
        }
        return retValue;
    }

    @Override
    public String getSourceType() {
        return "SNMP";
    }

    public int getSuffixLength() {
        return suffixLength;
    }

    public void setSuffixLength(int suffixLength) {
        this.suffixLength = suffixLength;
    }

    /**
     * @return the uptimeoid
     */
    public OID getUptimeoid() {
        return uptimeoid;
    }
}
