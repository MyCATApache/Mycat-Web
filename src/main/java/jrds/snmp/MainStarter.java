/**
 * 
 */
package jrds.snmp;

import java.io.IOException;

import jrds.starter.Starter;

import org.apache.logging.log4j.Level;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class MainStarter extends Starter {
    //Used to setup the log configuration of SNMP4J
    static {
        //Don't care about strict conformity
        SNMP4JSettings.setAllowSNMPv2InV1(true);
        org.snmp4j.log.LogFactory.setLogFactory(new Log4jLogFactory());
        //If not already configured, we filter it
      //  JrdsLoggerConfiguration.configureLogger("org.snmp4j", Level.ERROR);
    }
    public volatile Snmp snmp = null;

    public boolean start() {
        boolean started = false;
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
            started = true;
        } catch (IOException e) {
            log(Level.ERROR, e, "SNMP UDP Transport Mapping not started: %s", e);
            snmp = null;
        }
        return started;
    }

    public void stop() {
        try {
            snmp.close();
        } catch (IOException e) {
            log(Level.ERROR, e, "IO error while stop SNMP UDP Transport Mapping: %s", e);
        }
        snmp = null;
    }
}