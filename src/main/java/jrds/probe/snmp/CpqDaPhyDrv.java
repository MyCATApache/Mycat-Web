package jrds.probe.snmp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.rrd4j.core.Sample;
import org.snmp4j.smi.OID;

public class CpqDaPhyDrv extends RdsIndexedSnmpRrd {

	static final private String READ = "cpqDaPhyDrvReads64";
	static final private String WRITE = "cpqDaPhyDrvWrites64";
	
	static final private OID cpqDaPhyDrvCntlrIndex = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.1");
	static final private OID cpqDaPhyDrvIndex = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.2");
	static final private OID cpqDaPhyDrvHWrites = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.12");
	static final private OID cpqDaPhyDrvWrites = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.13");
	static final private OID cpqDaPhyDrvHReads = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.10");
	static final private OID cpqDaPhyDrvReads = new OID(".1.3.6.1.4.1.232.3.2.5.1.1.11");

	@Override
	public Collection<OID> getIndexSet()
	{
		Collection<OID> indexes = new HashSet<OID>(2);
		indexes.add(cpqDaPhyDrvCntlrIndex);
		indexes.add(cpqDaPhyDrvIndex);
		return indexes;
	}

	private double joinCounter32(Long high, Long low) {
		if(high != null && low != null )
			return high.longValue() << 32 +  low.longValue();
		else
			return Double.NaN;

	}

	/* (non-Javadoc)
	 * @see jrds.Probe#modifySample(org.rrd4j.core.Sample, java.util.Map)
	 */
	@Override
	public void modifySample(Sample oneSample, Map<OID, Object> values) {
		oneSample.setValue(READ, joinCounter32((Long)values.get(cpqDaPhyDrvHReads), (Long)values.get(cpqDaPhyDrvReads)));
		oneSample.setValue(WRITE, joinCounter32((Long)values.get(cpqDaPhyDrvHWrites), (Long)values.get(cpqDaPhyDrvWrites)));
	}
}
