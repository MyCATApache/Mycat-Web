<?xml version="1.0" encoding="UTF-8"?>
<host name="SNMP_${ip}_${port}" dnsName="${ip}">

	<connection type="jrds.snmp.SnmpConnection">
		<attr name="community">${community}</attr>
		<attr name="port">${port}</attr>
		<attr name="version">2</attr>
	</connection>
	<probe type="CpuRawLinux2" />
	<probe type="MemLinux" />
	<probe type="NumProcesses" />
	<probe type="IPx4" />
	<probe type="CpuRawLinux2" />
    <probe type="DiskIo64">
        <arg type="String" value="sd0"/>
        <arg type="OID" value=".1"/>
    </probe>
</host>