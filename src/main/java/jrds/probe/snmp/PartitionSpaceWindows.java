/*##########################################################################
 _##
 _##  $Id: NumProcesses.java 187 2006-01-18 19:08:14 +0100 (mer., 18 janv. 2006) fbacchella $
 _##
 _##########################################################################*/

package jrds.probe.snmp;

import org.apache.logging.log4j.Level;

/**
 * A extention of the partitionSpace probe, used tom manager the long naming convention
 * of disks in windows
 * For example :
 * HOST-RESOURCES-MIB::hrStorageDescr.2 = STRING: C:\ Label:Win2003  Serial Number 123abc
 * But we only want c:\
 * @param monitoredHost
 * @param indexKey
 * @author Fabrice Bacchella 
 */
public class PartitionSpaceWindows extends PartitionSpace {
    static final private char separator=' ';

    /**
     *only compare with String found before " " 
     * @see jrds.probe.snmp.RdsIndexedSnmpRrd#matchIndex(java.lang.String, java.lang.String)
     */
    public boolean matchIndex(String key) {
        int nameIndex = key.indexOf(separator);

        log(Level.DEBUG, "index split: found separator=\"%s\" in \"%s\" index=%s", separator, key, key);

        if (nameIndex != -1) {
            key = key.substring(0, nameIndex);
            log(Level.DEBUG, "index split: new name=\"%s\"", key);
        }
        return super.matchIndex(key);
    }
}
