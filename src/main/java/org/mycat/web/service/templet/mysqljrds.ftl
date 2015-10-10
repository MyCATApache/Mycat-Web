<?xml version="1.0" encoding="UTF-8"?>
<host name="D_${ip}_${port}_${dbName}" dnsName="${ip}">
	<connection type="jrds.probe.jdbc.JdbcConnection" name="${jrdsName}">
		<arg type="String" value="${username}" />
		<arg type="String" value="${password}" />
		<arg type="String" value="jdbc:mysql://${ip}:${port}/${dbName}" />
		<arg type="String" value="com.mysql.jdbc.Driver" />
	</connection>
<probe type="MySqlStatusGeneric" connection="${jrdsName}" />
</host>