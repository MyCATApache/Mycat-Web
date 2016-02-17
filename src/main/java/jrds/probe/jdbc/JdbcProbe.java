package jrds.probe.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jrds.Probe;
import jrds.ProbeDesc;
import jrds.Util;
import jrds.factories.ProbeBean;
import jrds.probe.IndexedProbe;
import jrds.probe.UrlProbe;

import org.apache.logging.log4j.Level;

@ProbeBean({"port", "user", "password"})
public abstract class JdbcProbe extends Probe<String, Number> implements UrlProbe, IndexedProbe {

    static final void registerDriver(String JdbcDriver) {
        try {
            Driver jdbcDriver = (Driver) Class.forName (JdbcDriver).newInstance();
            DriverManager.registerDriver(jdbcDriver);
        } catch (Exception e) {
            throw new RuntimeException("Can't register JDBC driver " + JdbcDriver, e);
        }	
    }

    static final void registerDriver(Class<?> JdbcDriver) {
        try {
            Driver jdbcDriver = (Driver) JdbcDriver.newInstance();
            DriverManager.registerDriver(jdbcDriver);
        } catch (Exception e) {
            throw new RuntimeException("Can't register JDBC driver " + JdbcDriver, e);
        }	
    }

    private int port;
    protected final JdbcStarter starter;

    public JdbcProbe() {
        super();
        starter = setStarter();
    }

    public JdbcProbe(ProbeDesc pd) {
        super(pd);
        starter = setStarter();
    }

    public void configure() {
        registerStarter(starter);        
    }

    public void configure(int port, String user, String passwd) {
        this.port = port;
        starter.setPasswd(passwd);
        starter.setUser(user);
        starter.setHost(getHost());
        registerStarter(starter);
    }

    public void configure(int port, String user, String passwd, String dbName) {
        this.port = port;
        starter.setDbName(dbName);
        starter.setPasswd(passwd);
        starter.setUser(user);
        starter.setHost(getHost());
        registerStarter(starter);
    }

    @Override
    public String getName() {
        return "jdbc-" + Util.stringSignature(getUrlAsString());
    }

    abstract JdbcStarter setStarter();

    public Map<String, Number> getNewSampleValues() {
        Map<String, Number> retValue = new HashMap<String, Number>(getPd().getSize());

        for(String query: getQueries()) {
            log(Level.DEBUG, "Getting %s", query); 
            retValue.putAll(select2Map(query));
        }
        return retValue;
    }

    public abstract List<String> getQueries();

    protected List<Map<String, Object>> parseRsVerticaly(ResultSet rs, boolean numFilter) throws SQLException {
        ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        log(Level.DEBUG, "Columns: %d", colCount);
        while(rs.next())  {
            Map<String, Object> row = new HashMap<String, Object>(colCount);
            String key =  rs.getObject(1).toString();
            Object oValue = rs.getObject(2).toString();
            row.put(key, oValue);
            values.add(row);
        }
        values.trimToSize();
        return values;
    }

    protected List<Map<String, Object>> parseRsHorizontaly(ResultSet rs, boolean numFilter) throws SQLException {
        ArrayList<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        while(rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>(colCount);
            for (int i = 1; i <= colCount; i++) {
                String key = rsmd.getColumnLabel(i);
                Object oValue = rs.getObject(i);
                if(numFilter) {
                    if(oValue instanceof Number)
                        oValue = (Number) oValue;
                    else
                        oValue = new Double(Double.NaN);
                }
                row.put(key, oValue);
            }
            values.add(row);
        }
        values.trimToSize();
        return values;
    }

    public abstract Map<String, Number> parseRs(ResultSet rs) throws SQLException;

    /**
     * Parse all the collumns of a query and return a List of Map
     * where the column name is the key
     * @param query
     * @param numFilter force all value to be a Number
     * @return a List of Map of values 
     */
    public Map<String, Number> select2Map(String query)
    {
        Map<String, Number> values = new HashMap<String, Number>();
        try {
            Statement stmt = starter.getStatment();
            if(stmt.execute(query)) {
                do {
                    values = parseRs(stmt.getResultSet());
                } while(stmt.getMoreResults());
            }
            else {
                log(Level.WARN, "Not a select query");
            }
            stmt.close();
        } catch (SQLException e) {
            log(Level.ERROR, e, "SQL Error: " + e);
        }
        return values;
    }

    public Map<String, Object> select2Map(String query, String keyCol, String valCol)
    {
        Map<String, Object> values = new HashMap<String, Object>();
        log(Level.DEBUG, "Getting %s", query); 
        Statement stmt = null;
        try {
            stmt = starter.getStatment();
            if(stmt.execute(query)) {
                do {
                    ResultSet rs = stmt.getResultSet();
                    while(rs.next()) {
                        String key = rs.getString(keyCol);
                        Number value;
                        Object oValue = rs.getObject(valCol);
                        if(oValue instanceof Number)
                            value = (Number) oValue;
                        else
                            value = new Double(Double.NaN);

                        values.put(key, value);
                    }
                } while(stmt.getMoreResults());
            }
            stmt.close();
        } catch (SQLException e) {
            log(Level.ERROR, e, "SQL Error: %s", e.getLocalizedMessage());
        }
        return values;
    }

    /**
     * @return Returns the port.
     */
    public Integer getPort() {
        return port;
    }
    /**
     * @param port The port to set.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return starter.getUser();
    }
    /**
     * @param user The user to set.
     */
    public void setUser(String user) {
        starter.setUser(user);
    }

    /**
     * @return Returns the user.
     */
    public String getPassword() {
        return starter.getPasswd();
    }
    /**
     * @param password The user to set.
     */
    public void setPassword(String password) {
        starter.setPasswd(password);
    }

    /**
     * @return Returns the dbName.
     */
    public String getDbName() {
        return starter.getDbName();
    }

    /**
     * @param dbName
     */
    public void setDbName(String dbName) {
        starter.setDbName(dbName);
    }

    public String getUrlAsString() {
        return starter.getUrlAsString();
    }

    @Override
    public boolean isCollectRunning() {
        return super.isCollectRunning() && starter.isStarted();
    }

    @Override
    public String getSourceType() {
        return "JDBC";
    }

    @Override
    public long getUptime() {
        return starter.getUptime();
    }

    public String getIndexName() {
        return starter.getDbName();
    }

    /* (non-Javadoc)
     * @see jrds.probe.UrlProbe#getUrl()
     */
    public URL getUrl(){
        URL newurl = null;
        try {
            newurl = new URL(getUrlAsString());
        } catch (MalformedURLException e) {
            log(Level.ERROR, e, "Invalid jdbc url: " + getUrlAsString());
        }
        return newurl;
    }

}
