/*##########################################################################
 _##
 _##  $Id$
 _##
 _##########################################################################*/

package jrds.probe.snmp;


/**
 * Used to just store some oid, with raw values
 * @author Fabrice Bacchella 
 * @version $Revision$,  $Date$
 */
public class Raw
extends RdsSnmpSimple {

	/* (non-Javadoc)
	 * @see jrds.probe.snmp.SnmpProbe#getSuffixLength()
	 */
	@Override
	public int getSuffixLength() {
		return 0;
	}

}
