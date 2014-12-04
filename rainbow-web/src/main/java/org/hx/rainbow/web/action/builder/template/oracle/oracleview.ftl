<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${nameSpace}">
	<select id="query" resultType="map" parameterType="map">
		SELECT
			<#list columns as property>
				${property.columnName}	as "${property.propertyName}"<#if property_has_next>, </#if>
			</#list>	
		FROM ${tableName}
		<where>
			<#list columns as property>
			<if test="${property.propertyName} != null">
				and ${property.columnName} = #${r"{"}${property.propertyName}${r"}"}
			</if>
			</#list>
		</where>
	</select>

	<select id="count" resultType="int" parameterType="map">
		SELECT COUNT(*) FROM ${tableName} 
		<where>
			<#list columns as property>
			<if test="${property.propertyName} != null">
				and ${property.columnName} = #${r"{"}${property.propertyName}${r"}"}
			</if>
			</#list>
		</where>
	</select>
</mapper>