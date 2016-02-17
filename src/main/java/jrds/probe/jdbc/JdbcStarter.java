package jrds.probe.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import jrds.HostInfo;
import jrds.starter.Resolver;
import jrds.starter.Starter;

import org.apache.logging.log4j.Level;

public abstract class JdbcStarter extends Starter {
	private Connection con;
	private String url;
	private String user;
	private String passwd;
	private String dbName = "";;

	public void setHost(HostInfo monitoredHost) {
		this.url = getUrlAsString();
	}
	
	public abstract String getUrlAsString();

	@Override
	public boolean start() {
		boolean started = false;
		Starter resolver = getLevel().find(Resolver.class);
		if(resolver.isStarted()) {
			Properties p = getProperties();
			p.put("user", user);
			p.put("password", passwd);
			try {
				DriverManager.setLoginTimeout(10);
				con = DriverManager.getConnection(url , user, passwd);
				started = true;
			} catch (SQLException e) {
				log(Level.ERROR, e, "SQL error: %s", e);
			}
		}
		return started;
	}

	@Override
	public void stop() {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log(Level.ERROR, e, "SQL error: %s", e);
			}
		}
		con = null;
	}

	public Statement getStatment() throws SQLException {
		return con.createStatement();
	}

	public Properties getProperties() {
		return new Properties();
	}

	@Override
	public Object getKey() {
		return url;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}
