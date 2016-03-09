package jrds.probe.jdbc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import jrds.factories.ProbeBean;
import jrds.starter.Connection;

import org.apache.logging.log4j.Level;

@ProbeBean({"user", "password", "url", "driverClass"})
public class JdbcConnection extends Connection<Statement> {

    static protected final void registerDriver(Class<? extends Driver> JdbcDriver) {
        try {
            Driver jdbcDriver = JdbcDriver.newInstance();
            DriverManager.registerDriver(jdbcDriver);
        } catch (Exception e) {
            throw new RuntimeException("Can't register JDBC driver " + JdbcDriver, e);
        }   
    }

    private java.sql.Connection con;
    private String user;
    private String passwd;
    private String driverClass = null;
    private String url;

    public JdbcConnection() {

    }

    public JdbcConnection(String user, String passwd, String url) {
        this.user = user;
        this.passwd = passwd;
        this.url = url;
        checkDriver(url);
    }

    public JdbcConnection(String user, String passwd, String url, String driverClass) {
        this.user = user;
        this.passwd = passwd;
        this.url = url;
        this.driverClass = driverClass;
        checkDriver(url);
    }

    public Statement getConnection() {
        try {
            return con.createStatement();
        } catch (SQLException e) {
            log(Level.ERROR, "JDBC Statment failed: " + e.getMessage());
            return null;
        }
    }

    public void checkDriver(String sqlurl) {
        try {
            if(driverClass != null && ! "".equals(driverClass)) {
                Class.forName(driverClass);
            }
            DriverManager.getDriver(sqlurl);
        } catch (SQLException e) {
            throw new RuntimeException("Error checking JDBC url " + sqlurl, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error checking JDBC url " + sqlurl, e);
        }
    }

    @Override
    public long setUptime() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean startConnection() {
        boolean started = false;
        if(getResolver().isStarted()) {
            String url = getUrl();
            try {
                DriverManager.setLoginTimeout(getTimeout());
                con = DriverManager.getConnection(url , user, passwd);
                started = true;
            } catch (SQLException e) {
                log(Level.ERROR, e, "Sql error for %s: %s" , url, e);
            }
        }
        return started;		
    }

    @Override
    public void stopConnection() {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log(Level.ERROR, e, "Error with %s: %s", getUrl(), e.getMessage());
            }
        }
        con = null;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return jrds.Util.parseTemplate(url, this, getLevel());
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the passwd
     */
    public String getPasswordd() {
        return passwd;
    }

    /**
     * @param passwd the passwd to set
     */
    public void setPassword(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the driverClass
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * @param driverClass the driverClass to set
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find JDBC driver " + driverClass, e);
        }
    }

}
