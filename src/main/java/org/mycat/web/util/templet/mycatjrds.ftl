<?xml version="1.0" encoding="UTF-8"?>
<host name="D_${ip}_${mangerPort}" dnsName="${ip}">
	<connection type="jrds.probe.jdbc.JdbcConnection" name="mycat">
		   <arg type="String" value="${username}" />
		   <arg type="String" value="${password}" />
		   <arg type="String" value="jdbc:mysql://${ip}:${mangerPort}/${dbName}" />
		   <arg type="String" value="com.mysql.jdbc.Driver" />
	</connection>
	<probe  type="MycatPerfProbe" connection="mycat" />
	<probe  type="MycatThreadPool" connection="mycat" />
	<probe  type="MycatMemory" connection="mycat" />
</host>