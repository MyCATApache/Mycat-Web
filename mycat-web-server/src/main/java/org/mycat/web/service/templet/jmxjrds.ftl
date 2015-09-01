<?xml version="1.0" encoding="UTF-8"?>
<host name="JMX_${jmxname}_${ip}_${port}" dnsName="${ip}">
        <connection type="jrds.probe.JMXConnection">
                <arg type="Integer" value="${port}"/>
		<#if username??  && username != "">
                <arg type="String" value="${username}"/>
		</#if>
		<#if password?? && password != "">
                <arg type="String" value="${password}"/>
		</#if>
        </connection>
        <probe type="JMXConcMarkSweepGC" label="${jmxname}" />
        <probe type="JMXThread" label="${jmxname}" />
        <probe type="JMXParallelGC" label="${jmxname}" />
        <probe type="JMXSerialGC" label="${jmxname}" />
</host>