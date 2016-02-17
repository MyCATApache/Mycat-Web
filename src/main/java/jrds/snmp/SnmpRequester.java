package jrds.snmp;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.*;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

/**
 * An enum to generate simple SNMP requesters
 * which gets a probe, a collection of ois
 * and make snmp requests based on those oid 
 * @author Fabrice Bacchella
 */
public enum SnmpRequester {
    /**
     * Collect a set of variable by append .0 to the OID of the oid
     * the returned OID are left unchanged
     */
    SIMPLE(){
        @Override
        public Map<OID, Object> doSnmpGet(SnmpConnection cnx, Collection<OID> oidsSet) throws IOException {
            VariableBinding[] vars = new VariableBinding[oidsSet.size()];
            int j = 0;
            for(OID i: oidsSet) {
                OID currentOid = (OID) i.clone();
                currentOid.append("0");
                vars[j++] = new VariableBinding(currentOid);
            }
            return doRequest(cnx, vars);
        }
    },
    /**
     *  A requester used to read an array of oid
     */
    TABULAR() {
        @Override
        public Map<OID, Object> doSnmpGet(SnmpConnection cnx, Collection<OID> oids) {

            Target snmpTarget = cnx.getConnection();
            Snmp snmp = cnx.getSnmp();

            if(cnx.isStarted() && snmpTarget != null && snmp != null) {
                TableUtils tableRet = new TableUtils(snmp, cnx.getPdufactory());
                tableRet.setMaxNumColumnsPerPDU(30);
                OID[] oidTab= new OID[oids.size()];
                oids.toArray(oidTab);
                SnmpVars retValue = new SnmpVars();
                for(TableEvent te: (Iterable<TableEvent>)tableRet.getTable(snmpTarget, oidTab, null, null)) {
                    if(! cnx.isStarted()) {
                        retValue = new SnmpVars();
                        break;
                    }
                    if(! te.isError()) {
                        retValue.join(te.getColumns());
                    }
                }
                return retValue;
            }
            return Collections.emptyMap();
        }        
    },
    /**
     *  A requester used to read an tree of oid
     */
    TREE() {
        @Override
        public Map<OID, Object> doSnmpGet(SnmpConnection cnx, Collection<OID> oids) {

            Target snmpTarget = cnx.getConnection();
            Snmp snmp = cnx.getSnmp();
            if(cnx.isStarted() && snmpTarget != null && snmp != null) {
                SnmpVars retValue = new SnmpVars();
                TreeUtils treeRet = new TreeUtils(snmp, cnx.getPdufactory());
                for(OID rootOid : oids) {
                    List<TreeEvent> subOids = treeRet.getSubtree(snmpTarget, rootOid);
                    for(TreeEvent te: subOids) {
                        retValue.join(te.getVariableBindings());
                    }
                }
                return retValue;
            }
            return Collections.emptyMap();
        }        
    },
    /**
     * The simplest requester
     * Just get a collection of oid and return the associated value
     */
    RAW() {
        @Override
        public Map<OID, Object> doSnmpGet(SnmpConnection cnx, Collection<OID> oidsSet) throws IOException {
            VariableBinding[] vars = new VariableBinding[oidsSet.size()];
            int j = 0;
            for(OID currentOid: oidsSet) {
                vars[j++] = new VariableBinding(currentOid);
            }
            return doRequest(cnx, vars);
        }        
    };

    static private final Logger logger = LogManager.getLogger(SnmpRequester.class);

    /**
     * The method that need to be implemented to do the request
     * @param cnx The connection used for the request
     * @param oidsSet a <code>collection</code> of OID to be requested
     * @return a map of the snmp values read
     * @throws IOException
     */
    public abstract Map<OID, Object> doSnmpGet(SnmpConnection cnx, Collection<OID> oidsSet) throws IOException;

    private static final Map<OID, Object> doRequest(SnmpConnection cnx, VariableBinding[] vars) throws IOException {
        Map<OID, Object> snmpVars = Collections.emptyMap();

        Target snmpTarget = cnx.getConnection();

        PDU requestPDU = cnx.getPdufactory().createPDU(snmpTarget);
        requestPDU.addAll(vars);

        //If no oid to collect, nothing to do
        if(requestPDU.size() < 1) 
            return Collections.emptyMap();

        boolean doAgain = true;
        Snmp snmp = cnx.getSnmp();
        while(doAgain && cnx.isStarted()) {
            ResponseEvent re = snmp.send(requestPDU, snmpTarget);
            if(re == null) {
                throw new IOException("SNMP Timeout");
            }
            PDU response = re.getResponse();
            if (response != null && response.getErrorStatus() == SnmpConstants.SNMP_ERROR_SUCCESS){
                snmpVars = new SnmpVars(response);
                doAgain = false;
            }	
            else if(response == null) {
                throw new IOException("SNMP Timeout");
            }
            else {
                int index = response.getErrorIndex() - 1;
                VariableBinding vb = response.get(index);
                logger.warn(response.getErrorStatusText() + " on " + vb.getOid().toString());
                /*If there is still variable to get, we try again*/
                if(requestPDU.size() > 1) {
                    requestPDU = response;
                    response = null;
                    requestPDU.remove(index);
                }
                else
                    doAgain = false;
            }
        };
        return snmpVars;
    }

}