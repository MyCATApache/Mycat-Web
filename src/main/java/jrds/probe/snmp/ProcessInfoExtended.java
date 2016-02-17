package jrds.probe.snmp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jrds.factories.ProbeBean;
import jrds.snmp.SnmpVars;
import jrds.snmp.TabularIterator;

import org.apache.logging.log4j.Level;
import org.rrd4j.core.Sample;
import org.snmp4j.smi.OID;

/**
 * A class to probe info about a process, using MIB-II
 * @author Fabrice Bacchella 
 */
@ProbeBean({"index",  "pattern"})
public class ProcessInfoExtended extends RdsIndexedSnmpRrd {
    static final private OID hrSWRunPath = new OID(".1.3.6.1.2.1.25.4.2.1.4");
    static final private OID hrSWRunParameters = new OID(".1.3.6.1.2.1.25.4.2.1.5");
    static final private OID hrSWRunPerfMem = new OID(".1.3.6.1.2.1.25.5.1.1.2");
    static final private OID  hrSWRunPerfCPU = new OID(".1.3.6.1.2.1.25.5.1.1.1");
    static final private String MIN = "Minimum";
    static final private String MAX = "Maximum";
    static final private String AVERAGE = "Average";
    static final private String NUM = "Number";
    static final private String CPU = "Cpu";

    private Pattern pattern = Pattern.compile("^$");

    /**
     * @param monitoredHost
     */
    public boolean configure(String indexName, String patternString) {
        try {
            this.pattern = Pattern.compile(patternString);
        } catch (java.util.regex.PatternSyntaxException e) {
            log(Level.ERROR, e, "Invalid pattern '%s': %s", patternString, e);
            return false;
        }
        return super.configure(indexName);
    }

    @Override
    public Collection<OID> getIndexSet() {
        Collection<OID> indexes = new HashSet<OID>(2);
        indexes.add(hrSWRunPath);
        indexes.add(hrSWRunParameters);
        return indexes;
    }

    /**
     * @see jrds.probe.snmp.SnmpProbe#getOidSet()
     */
    public Set<OID> getOidSet() {
        Set<OID> retValue = new HashSet<OID>();
        for(int[] indexArray: getProcsOID()) {
            retValue.addAll(makeIndexed(getOidNameMap().keySet(), indexArray));
        }
        return retValue;
    }

    public Collection<int[]> getProcsOID() {
        boolean found = false;
        Collection<OID> soidSet= getIndexSet();

        Collection<int[]>  oids = new HashSet<int[]>();
        TabularIterator ti = new TabularIterator(getConnection(), soidSet);
        for(SnmpVars s: ti) {
            List<OID> lk = new ArrayList<OID>(s.keySet());
            Collections.sort(lk);
            StringBuffer cmdBuf = new StringBuffer();
            for(OID oid: lk) {
                cmdBuf.append(s.get(oid));
                cmdBuf.append(" ");
            }
            if(pattern.matcher(cmdBuf.toString().trim()).matches()) {
                int[] index = new int[1];
                index[0] = lk.get(0).last();
                oids.add(index);
                found = true;
            }
        }
        if(! found) {
            log(Level.ERROR, "index for %s not found for host %s", indexKey, getHost().getName());
            oids = Collections.emptySet();
        }
        else {
            log(Level.DEBUG, "found %d processes", oids.size());
            log(Level.TRACE, "processes indexes found: %s", oids);
        }
        return oids;
    }

    /* (non-Javadoc)
     * @see jrds.Probe#modifySample(org.rrd4j.core.Sample, java.util.Map)
     */
    @Override
    public void modifySample(Sample oneSample, Map<OID, Object> snmpVars) {
        log(Level.TRACE, "Will uses snmp values from %s", snmpVars);
        double max = 0;
        double min = Double.MAX_VALUE;
        double average = 0;
        int nbvalue = 0;
        double cpuUsed = 0;
        for(Map.Entry<OID, Object> e: ((Map<OID, Object>)snmpVars).entrySet()) {
            OID oid = e.getKey();
            if(oid.startsWith(hrSWRunPerfMem)) {
                double value = ((Number)e.getValue()).doubleValue() * 1024;
                max = Math.max(max, value);
                min = Math.min(min, value);
                average += value;
                nbvalue++;
            }
            else if(oid.startsWith(hrSWRunPerfCPU)) {
                cpuUsed += ((Number)e.getValue()).doubleValue() / 100.0;
            }
        }
        average /= nbvalue;
        oneSample.setValue(NUM, nbvalue);
        oneSample.setValue(MAX, max);
        oneSample.setValue(MIN, min);
        oneSample.setValue(AVERAGE, average);
        oneSample.setValue(CPU, cpuUsed);		
    }

    /* (non-Javadoc)
     * @see jrds.probe.snmp.SnmpProbe#getSuffixLength()
     */
    @Override
    public int getSuffixLength() {
        return 0;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern.pattern();
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String patternString) {
        this.pattern = Pattern.compile(patternString);
    }

}

