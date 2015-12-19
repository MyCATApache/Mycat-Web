package org.mycat.web.model.cluster;

public class DataHost {
	private String name;
	private int maxcon;
	private int mincon;
	private int balance;
	private int writetype;
	private String dbtype;
	private String dbDriver;
	private int switchType;
	private int slaveThreshold;
	private String heartbeantSQL;
	private String mysqlGroup;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxcon() {
		return maxcon;
	}
	public void setMaxcon(int maxcon) {
		this.maxcon = maxcon;
	}
	public int getMincon() {
		return mincon;
	}
	public void setMincon(int mincon) {
		this.mincon = mincon;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
    
	public int getWritetype() {
		return writetype;
	}
	public void setWritetype(int writetype) {
		this.writetype = writetype;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getDbDriver() {
		return dbDriver;
	}
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	public int getSwitchType() {
		return switchType;
	}
	public void setSwitchType(int switchType) {
		this.switchType = switchType;
	}
	public int getSlaveThreshold() {
		return slaveThreshold;
	}
	public void setSlaveThreshold(int slaveThreshold) {
		this.slaveThreshold = slaveThreshold;
	}
	public String getHeartbeantSQL() {
		return heartbeantSQL;
	}
	public void setHeartbeantSQL(String heartbeantSQL) {
		this.heartbeantSQL = heartbeantSQL;
	}
	public String getMysqlGroup() {
		return mysqlGroup;
	}
	public void setMysqlGroup(String mysqlGroup) {
		this.mysqlGroup = mysqlGroup;
	}
	

}
