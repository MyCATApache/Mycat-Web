package jrds;

/**
 * This interface is used to indicate that a probe use a connexion
 * Any class using this interface should implement the following code :
 * <ul>
 * <li> it should have this field
 * <pre>
 * 	private String connectionName = ConnectionClass.class.getName();
 * </pre>
 * <li> the method getNewSampleValues should begin with the following code :
 * <pre>
 * 	ConnectionClass cnx = (ConnectionClass) getStarters().find(connectionName);
 *	if( !cnx.isStarted()) {
 *		return Collections.EMPTY_MAP;
 *	}
 *	//Uptime is collected only once, by the connexion
 *	setUptime(cnx.getUptime());
 * </pre>
 * <li> It should implements the two following methods
 * <pre>
 * 	public String getConnectionName() {
 *		return connectionName;
 *	}
 *	public void setConnectionName(String connectionName) {
 *		this.connectionName = connectionName;
 *	}
 * </pre>
 * </ul>
 * 
 * This intereface is used by the Probe class to check if it needs to check the connection started, so there is no need
 * to overwrite the isStarted method.
 * 
 * @author Fabrice Bacchella 
 * @version $Revision: 407 $,  $Date: 2007-02-22 18:48:03 +0100 (jeu., 22 févr. 2007) $
 */
public interface ConnectedProbe {
	public String getConnectionName();
	public void setConnectionName(String connection);
}
