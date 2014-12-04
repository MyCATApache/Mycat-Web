<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://org.opencloudb/">

	<#macro recurse_macro childlist>
		<#list childlist as child>
		  <childTable name="${child.name?default('')}" joinKey="${child.joinkey?default('')}" parentKey="${child.parentkey?default('')}">
		 <#if (child.childlist)??>
		 <@recurse_macro childlist=child.childlist />
		 </#if>
		  </childTable>
	</#list>
	</#macro>
	
		<#list schemaList as schema>
	<schema name="${schema.name}">
		<#if (schema.tableList)??>
		<#list schema.tableList as table>
		<#if table.type=='global'>
		<table name="${table.name?default('')}" type="${table.type}" dataNode="${table.datanode?default('')}" />
		<#else>		
		<table name="${table.name?default('')}" dataNode="${table.datanode?default('')}" rule="${table.rule}">
		<#if (table.childlist)??>
		<@recurse_macro childlist=table.childlist />
		</#if>
		</table>
		</#if>
		</#list>
		</#if>
		</schema>
		</#list>
	
	<#list dataNode as node>
		<dataNode name="${node.name?default('')}" database="${node.database?default('')}" dataHost="${node.datahost?default('')}" />
	</#list>
	
	<#list dataHost as host>
		<dataHost name="${host.name?default('')}" maxCon="${host.maxCon}" minCon="${host.minCon}" balance="${host.balance?default('')}">
		<#if (host.hearbeat)??>
		<heartbeat>${host.hearbeat}</heartbeat>
		</#if>
		<#if (host.writeList)??>
		<#list host.writeList as write>
		<writeHost host="${write.host?default('')}" url="${write.url?default('')}" user="${write.user?default('')}" password="${write.password?default('')}">
			<#if (write.readList)??>
			<#list write.readList as read>
			<readHost host="${read.host?default('')}" url="${read.url?default('')}" user="${read.url?default('')}" password="${read.password?default('')}" />
			</#list>
			</#if>
		</writeHost>
		</#list>
		</#if>
		</dataHost>
	</#list>

</mycat:schema>
