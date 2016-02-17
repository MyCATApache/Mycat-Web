package jrds.snmp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jrds.factories.ProbeBean;
import jrds.starter.Connection;
import jrds.starter.Resolver;

import org.apache.logging.log4j.Level;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;

@ProbeBean({"community", "port", "version", "ping"})
public class SnmpConnection extends Connection<Target> {
    static final String TCP = "tcp";
    static final String UDP = "udp";
    static final private OID hrSystemUptime = new OID(".1.3.6.1.2.1.25.1.1.0");
    static final private OID sysUpTimeInstance = new OID(".1.3.6.1.2.1.1.3.0");
    static final private OID sysDescr = new OID("1.3.6.1.2.1.1.1.0");
    static final private PDUFactory pdufactory = new DefaultPDUFactory(PDU.GET);

    private int version = SnmpConstants.version2c;
    private String proto = UDP;
    private int port = 161;
    private String community = "public";
    private OID ping = sysDescr;
    //A default value for the uptime OID, from the HOST-RESSOURCES MIB
    private OID uptimeOid = hrSystemUptime;
    private Target snmpTarget;

    @Override
    public Target getConnection() {
        return snmpTarget;
    }

    @Override
    public boolean startConnection() {
        Resolver resolver = getLevel().find(Resolver.class);
        if(!resolver.isStarted())
            return false;

        if(! getLevel().find(MainStarter.class).isStarted())
            return false;

        Address address;

        if(UDP.equals(proto.toLowerCase())) {
            address = new UdpAddress(resolver.getInetAddress(), port);
        }
        else if(TCP.equals(proto.toLowerCase())) {
            address = new TcpAddress(resolver.getInetAddress(), port);
        }
        else {
            return false;
        }
        if(community != null && address != null) {
            snmpTarget = new CommunityTarget(address, new OctetString(community));
            snmpTarget.setVersion(version);
            snmpTarget.setTimeout(getLevel().getTimeout() * 1000 / 2);
            snmpTarget.setRetries(1);
        }
        //Do a "snmp ping", to check if host is reachable
        try {
            PDU requestPDU = DefaultPDUFactory.createPDU(snmpTarget, PDU.GET);
            requestPDU.addOID(new VariableBinding(ping));
            Snmp snmp = getSnmp();
            ResponseEvent re = snmp.send(requestPDU, snmpTarget);
            if(re == null)
                throw new IOException("SNMP Timeout");
            PDU response = re.getResponse();
            if(response == null || re.getError() != null ) {
                Exception snmpException = re.getError();
                if(snmpException == null)
                    snmpException = new IOException("SNMP Timeout");
                throw snmpException;
            }
            //Everything went fine, host is reachable, authentication is working
            return true;
        } catch (Exception e) {
            log(Level.ERROR, e, "Unable to reach host: %s", e);
        }
        snmpTarget = null;
        return false;
    }

    @Override
    public void stopConnection() {
        snmpTarget = null;        
    }

    @Override
    public long setUptime() {
        Set<OID> upTimesOids = new HashSet<OID>(2);
        upTimesOids.add(uptimeOid);
        //Fallback uptime OID, it should be always defined, from SNMPv2-MIB
        upTimesOids.add(sysUpTimeInstance);
        return readUptime(upTimesOids);
    }

    public long readUptime(Set<OID> upTimesOids) {
        try {
            for(OID uptimeoid: upTimesOids) {
                PDU requestPDU = DefaultPDUFactory.createPDU(snmpTarget, PDU.GET);
                requestPDU.addOID(new VariableBinding(uptimeoid));
                Snmp snmp = getSnmp();
                ResponseEvent re = snmp.send(requestPDU, snmpTarget);
                if(re == null)
                    throw new IOException("SNMP Timeout");
                PDU response = re.getResponse();
                if(response == null || re.getError() != null ) {
                    Exception snmpException = re.getError();
                    if(snmpException == null)
                        snmpException = new IOException("SNMP Timeout");
                    throw snmpException;
                }
                Object value = new SnmpVars(response).get(uptimeoid);
                if(value instanceof Number) {
                    return ((Number) value).longValue();
                }
            }
        } catch (Exception e) {
            log(Level.ERROR, e, "Unable to get uptime: %s", e);
        }
        return 0;        
    }
    
    public Snmp getSnmp() {
        Snmp retValue = null;
        retValue = getLevel().find(MainStarter.class).snmp;
        return retValue;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version + 1;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version - 1;
    }

    /**
     * @return the proto
     */
    public String getProto() {
        return proto;
    }

    /**
     * @param proto the proto to set
     */
    public void setProto(String proto) {
        this.proto = proto;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the community
     */
    public String getCommunity() {
        return community;
    }

    /**
     * @param community the community to set
     */
    public void setCommunity(String community) {
        this.community = community;
    }

    /**
     * @return the pdufactory
     */
    public PDUFactory getPdufactory() {
        return pdufactory;
    }

    @Override
    public String toString() {
        return "snmp:" + proto + "://" + getHostName() + ":" + port;
    }

    /**
     * @return the ping
     */
    public OID getPing() {
        return ping;
    }

    /**
     * @param ping the ping to set
     */
    public void setPing(OID ping) {
        this.ping = ping;
    }

}
