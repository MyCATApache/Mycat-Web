<?xml version="1.0" encoding="UTF-8"?>
<host name="MYSQL_${ip}_${port}_${dbname}" dnsName="${ip}">
	<connection type="jrds.probe.jdbc.JdbcConnection" name="${dbname}">
		<arg type="String" value="${username}" />
		<arg type="String" value="${password}" />
		<arg type="String" value="jdbc:mysql://${ip}:${port}/${dbname}" />
		<arg type="String" value="com.mysql.jdbc.Driver" />
	</connection>
<probe type="MySqlStatusGeneric" connection="${dbname}" />
</host>