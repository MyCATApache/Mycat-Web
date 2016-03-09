/*##########################################################################
_##
_##  $Id$
_##
_##########################################################################*/

package jrds.probe.snmp;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.rrd4j.core.Sample;
import org.snmp4j.smi.OID;

/**
 * @author Fabrice Bacchella 
 */
public class ProcessStatusHostResources extends RdsSnmpSimple {
    static final private String RUNNING="running";
    static final private int RUNNINGINDEX = 1;
    static final private String RUNNABLE="runnable";
    static final private int RUNNABLEINDEX = 2;
    static final private String NOTRUNNABLE="notRunnable";
    static final private int NOTRUNNABLEINDEX = 3;
    static final private String INVALID="invalid";
    static final private int INVALIDINDEX = 4;

    /* (non-Javadoc)
     * @see jrds.Probe#modifySample(org.rrd4j.core.Sample, java.util.Map)
     */
    @Override
    public void modifySample(Sample oneSample, Map<OID, Object> snmpVars) {
        int running = 0;
        int runnable = 0;
        int notRunnable = 0;
        int invalid = 0;
        for(Object status: snmpVars.values()){
            if(status == null)
                continue;
            if(! (status instanceof Number))
                continue;
            int state = ((Number)status).intValue();
            if(RUNNINGINDEX == state)
                running++;
            else if(RUNNABLEINDEX == state)
                runnable++;
            else if(NOTRUNNABLEINDEX == state)
                notRunnable++;
            else if(INVALIDINDEX == state)
                invalid++;
        }
        oneSample.setValue(RUNNING, running);
        oneSample.setValue(RUNNABLE, runnable);
        oneSample.setValue(NOTRUNNABLE, notRunnable);
        oneSample.setValue(INVALID, invalid);
    }

    /* (non-Javadoc)
     * @see jrds.probe.snmp.SnmpProbe#getSuffixLength()
     */
    @Override
    public int getSuffixLength() {
        return 0;
    }

}
