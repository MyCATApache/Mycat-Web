/*##########################################################################
_##
_##  $Id$
_##
_##########################################################################*/

package jrds.probe.snmp;

import java.util.Map;

import org.rrd4j.core.Sample;
import org.snmp4j.smi.OID;

/**
 * @author Fabrice Bacchella 
 */
public class ProcessStatusSolaris extends RdsSnmpSimple {
    static final private String RUNNABLE="R";
    static final private String STOPPED="T";
    static final private String INPAGEWAIT="P";
    static final private String NONINTERRUPTABLEWAIT="D";
    static final private String SLEEPING="S";
    static final private String IDLE="I";
    static final private String ZOMBIE="Z";


    /* (non-Javadoc)
     * @see jrds.Probe#modifySample(org.rrd4j.core.Sample, java.util.Map)
     */
    @Override
    public void modifySample(Sample oneSample, Map<OID, Object> snmpVars) {
        int runnable = 0;
        int stopped = 0;
        int inPageWait = 0;
        int nonInterruptableWait = 0;
        int sleeping = 0;
        int idle = 0;
        int zombie = 0;
        for(Object val: snmpVars.values()) {
            String state = val.toString();    
            if(RUNNABLE.equals(state))
                runnable++;
            else if(STOPPED.equals(state))
                stopped++;
            else if(INPAGEWAIT.equals(state))
                inPageWait++;
            else if(NONINTERRUPTABLEWAIT.equals(state))
                nonInterruptableWait++;
            else if(SLEEPING.equals(state))
                sleeping++;
            else if(IDLE.equals(state))
                idle++;
            else if(ZOMBIE.equals(state))
                zombie++;

        }
        oneSample.setValue(RUNNABLE, runnable);
        oneSample.setValue(STOPPED, stopped);
        oneSample.setValue(INPAGEWAIT, inPageWait);
        oneSample.setValue(NONINTERRUPTABLEWAIT, nonInterruptableWait);
        oneSample.setValue(SLEEPING, sleeping);
        oneSample.setValue(IDLE, idle);
        oneSample.setValue(ZOMBIE, zombie);
    }
}
