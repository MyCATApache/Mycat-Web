package jrds;

import java.util.Collections;
import java.util.Map;

import jrds.starter.Connection;

import org.apache.logging.log4j.Level;

public abstract class ProbeConnected<KeyType, ValueType, ConnectionClass extends jrds.starter.Connection<?>> extends Probe<KeyType, ValueType> implements ConnectedProbe {
    private String connectionName;

    public ProbeConnected(String connectionName) {
        super();
        this.connectionName = connectionName;
        log(Level.DEBUG, "New connected probe using %s", connectionName);
    }

    public Boolean configure() {
        return true;
    }

    /* (non-Javadoc)
     * @see jrds.ConnectedProbe#getConnectionName()
     */
    public String getConnectionName() {
        return connectionName;
    }

    /* (non-Javadoc)
     * @see jrds.ConnectedProbe#setConnectionName(java.lang.String)
     */
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public ConnectionClass getConnection() {
        return find(getConnectionName());
    }

    /**
     * 
     * The method that return a map of data collected.<br>
     * It should return return as raw as possible, they can even be opaque data tied to the probe.
     * the key is resolved using the <code>ProbeDesc</code>. A key not associated with an existent datastore will generate a warning
     * but will not prevent the other values to be stored.<br>
     * the value should be a <code>java.lang.Number<code><br>
     * It also set the uptime for the probe, using connection data. One should override setUptime if a probe use a specific uptime, that can't be provided
     * using the connection.
     * @return the map of collected object
     */
    @Override
    public Map<KeyType, ValueType> getNewSampleValues() {
        ConnectionClass cnx = getConnection();
        if(cnx == null) {
            log(Level.WARN, "No connection found with name %s", getConnectionName());
        }
        log(Level.DEBUG, "find connection %s", connectionName);
        if( cnx == null || !cnx.isStarted()) {
            return Collections.emptyMap();
        }
        //Uptime is collected only once, by the connexion
        setUptime(cnx);
        return getNewSampleValuesConnected(cnx);
    }

    /**
     * This method is used by getNewSampleValues to define the uptime for the probe.<br>
     * The default way is to read it using the connection. It might be overridden if needed.
     * @param cnx
     */
    protected void setUptime(ConnectionClass cnx) {
        setUptime(cnx.getUptime());
    }


    public abstract Map<KeyType, ValueType> getNewSampleValuesConnected(ConnectionClass cnx);

    /* (non-Javadoc)
     * @see jrds.Probe#isCollectRunning()
     */
    @Override
    public boolean isCollectRunning() {
        String cnxName = getConnectionName();
        Connection<?> cnx = find(Connection.class, cnxName);
        if(getNamedLogger().isTraceEnabled())
            log(Level.TRACE, "Connection %s started: %s", cnxName, (cnx != null ? Boolean.toString(cnx.isStarted()) : "null") );
        if(cnx == null || ! cnx.isStarted())
            return false;
        return super.isCollectRunning();
    }

}
