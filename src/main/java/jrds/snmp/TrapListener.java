package jrds.snmp;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jrds.factories.ProbeBean;
import jrds.probe.PassiveProbe;
import jrds.starter.Listener;

import org.apache.logging.log4j.Level;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

@ProbeBean({"port", "community", "proto", "version"})
public class TrapListener extends Listener<CommandResponderEvent, OID> {
    static final String TCP = "tcp";
    static final String UDP = "udp";
    private int version = SnmpConstants.version2c;
    private String proto = UDP;
    private int port = 1162;
    private String community = "public";
    private final CommandResponder trapReceiver =  new CommandResponder() {
        public synchronized void processPdu(CommandResponderEvent e) {
            parsePDU(e);
        }
    };

    Snmp snmp = null;

    /* (non-Javadoc)
     * @see jrds.starter.Listener#start()
     */
    @Override
    public boolean start() {
        try {
            TransportMapping<?> transport = null;
            if(UDP.equals(proto)) {
                UdpAddress listenAddress = new UdpAddress(port);
                transport = new DefaultUdpTransportMapping(listenAddress, true);
            }
            snmp = new Snmp(transport);
            snmp.addCommandResponder(trapReceiver);
            transport.listen();
            return super.start();
        } catch (IOException e) {
            log(Level.ERROR, e, "failed to start the trap listener: %s", e.getMessage());
            return false;
        }
    }

    /* (non-Javadoc)
     * @see jrds.starter.Listener#stop()
     */
    @Override
    public void stop() {
        super.stop();
        try {
            snmp.close();
        } catch (IOException e) {
        }
        snmp = null;
    }

    @Override
    public void listen() throws IOException, InterruptedException {
        Thread.currentThread().join();
    }

    private void parsePDU(CommandResponderEvent ev) {
        log(Level.DEBUG, "trap received: %s", ev);
        PassiveProbe<OID> pp = findProbe(ev);
        Map<OID, Object> vars = new SnmpVars(ev.getPDU().toArray());
        Map<OID, Number> oids = new HashMap<OID, Number>(vars.size());
        for(Map.Entry<OID, Object> e: vars.entrySet()) {
            if(e.getValue() instanceof Number) {
                oids.put(e.getKey(), (Number) e.getValue());
            }
        }
        pp.store(new Date(), oids);
    }

    @Override
    protected String getHost(PassiveProbe<OID> pp) {
        return pp.getHost().getDnsName();
    }

    @Override
    public String identifyHost(CommandResponderEvent message) {
        IpAddress ip = (IpAddress) message.getPeerAddress();
        return ip.getInetAddress().getCanonicalHostName();
    }

    @Override
    public String identifyProbe(CommandResponderEvent message) {
        return "generictrap";
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version + 1;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
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
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
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

    @Override
    public String getSourceType() {
        return "Trap Listener";
    }

}
