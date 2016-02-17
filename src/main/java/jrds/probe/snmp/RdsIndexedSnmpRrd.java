package jrds.probe.snmp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jrds.factories.ProbeBean;
import jrds.probe.IndexedProbe;
import jrds.snmp.SnmpRequester;

import org.apache.logging.log4j.Level;
import org.snmp4j.smi.OID;


/**
 * @author Fabrice Bacchella
 */
@ProbeBean({"index",  "oid"})
public class RdsIndexedSnmpRrd extends SnmpProbe implements IndexedProbe {

    static public final String INDEXOIDNAME="indexOid";

    OID key;
    String indexKey;
    private OID indexOid;

    static final SnmpRequester indexFinder = SnmpRequester.TABULAR;
    static final SnmpRequester valueFinder = SnmpRequester.RAW;

    public boolean configure(String indexKey) {
        this.indexKey = indexKey;
        return configure();
    }

    public boolean configure(Integer indexKey) {
        this.indexKey = String.valueOf(indexKey);
        return configure();
    }

    /**
     * If the key type is an OID, it is directly the OID suffix, no look up will be done
     * @param indexKey
     */
    public boolean configure(OID indexKey) {
        this.indexKey = indexKey.toString();
        this.key = indexKey;
        return configure();
    }

    public boolean configure(String keyName, OID indexKey) {
        this.key = indexKey;
        this.indexKey = keyName;
        return configure();
    }

    protected SnmpRequester getSnmpRequester() {
        return valueFinder;
    }

    /* (non-Javadoc)
     * @see jrds.probe.snmp.SnmpProbe#readSpecific()
     */
    @Override
    public boolean readSpecific() {
        getPd().addSpecific(REQUESTERNAME, "RAW");
        String oidString =  getPd().getSpecific(INDEXOIDNAME);
        if(oidString != null && oidString.length() > 0) {
            indexOid = new OID(oidString);
        }
        return super.readSpecific();
    }

    public String getIndexName(){
        return indexKey;
    }

    /**
     * Build a set of OID to collect by appending the OID index to the given set of OID
     * @param oids the OID used for the base
     * @param index the OID suffix to append
     * @return a set of OID that will be collected
     */
    protected Set<OID> makeIndexed(Collection<OID> oids, int[] index)
    {
        Set<OID> oidToGet = new HashSet<OID>(oids.size());
        for(OID oidCurs: oids) {
            OID oidBuf = new OID(oidCurs.getValue(), index);
            oidToGet.add(oidBuf);
        }
        return oidToGet;
    }

    /**
     * Return the set of indexes OID
     * @return the set of OID used as indexes
     */
    protected Collection<OID> getIndexSet() {
        if(indexOid != null)
            return  Collections.singleton(indexOid);
        return Collections.emptySet();
    }

    public int getIndexPrefixLength() {
        return indexOid.size();
    }

    /**
     * Generate the index suffix for the probe
     * @return the OID suffix for the index
     */
    public int[] setIndexValue() 
    {
        //If we already have the key, no need to search for it
        if(key != null) {
            return key.getValue();
        }
        else {
            try {
                Collection<OID> soidSet = getIndexSet();
                Map<OID, Object> somevars = indexFinder.doSnmpGet(getConnection(), soidSet);

                for(Map.Entry<OID, Object> e: somevars.entrySet()) {
                    OID tryoid = e.getKey();
                    if(e.getValue() != null && matchIndex(somevars.get(tryoid))) {
                        int[] index = Arrays.copyOfRange(tryoid.getValue(), getIndexPrefixLength(), tryoid.size());
                        setSuffixLength(tryoid.size() - getIndexPrefixLength());
                        return index;
                    }
                }
                log(Level.ERROR, "index for %s not found", indexKey);
            } catch (IOException e) {
                log(Level.ERROR, e, "index for %s not found because of %s", indexKey, e);
            }
        }
        return new int[0];
    }

    /**
     * This method check if the tried value match the index
     * @param index the index value 
     * @param key the found key tried
     * @return
     */
    public boolean matchIndex(Object readKey) {
        return indexKey.equals(readKey.toString());
    }

    /**
     * @see jrds.probe.snmp.SnmpProbe#getOidSet()
     */
    public Set<OID> getOidSet() {
        Set<OID> retValue = null;
        int[] indexArray = setIndexValue();
        if(indexArray != null)
            retValue = makeIndexed(getOidNameMap().keySet(), indexArray);
        return retValue;
    }

    /**
     * @return the indexKey
     */
    public String getIndex() {
        return indexKey;
    }

    /**
     * @param indexKey the indexKey to set
     */
    public void setIndex(String indexKey) {
        this.indexKey = indexKey;
    }

    /**
     * @return the indexOid
     */
    public OID getOid() {
        return key;
    }

    /**
     * @param indexOid the indexOid to set
     */
    public void setOid(OID indexOid) {
        this.key = indexOid;
    }

}
