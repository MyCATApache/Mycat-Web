<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${nameSpace}">

	<select id="load" resultType="map" parameterType="map">
		SELECT 
			<#list columns as property>
				${property.columnName}	as "${property.propertyName}"<#if property_has_next>, </#if> <#if property.columnComment?trim != ""><!-- ${property.columnComment} --></#if>
			</#list>		
		FROM ${tableName}
		<where>
			<#list pks as property>
				and ${property.pk} = #${r"{"}${property.propertyName}${r"}"}
			</#list>
		</where>

	</select>

	<select id="query" resultType="map" parameterType="map">
		SELECT
			<#list columns as property>
				${property.columnName}	as "${property.propertyName}"<#if property_has_next>, </#if> <#if property.columnComment?trim != ""><!-- ${property.columnComment} --></#if>
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

	<#-- 插入操作 -->
	<insert id="insert" parameterType="map">
		INSERT INTO ${tableName} (
		<#list columns as property>
			${property.columnName}<#if property_has_next>,</#if><#if property.columnComment?trim != "">  <!-- ${property.columnComment} --></#if>
		</#list>
		)
		VALUES ( 
		<#list columns as property>
			#${r"{"}${property.propertyName}, jdbcType=${property.dataType}${r"}"}<#if property_has_next>, 
		</#if>
		</#list>
		)
	</insert>
  
	<#-- 删除操作 -->
	<delete id="delete" parameterType="map">
		DELETE FROM ${tableName} 
		<where> 
		<#list pks as property> 
			${property.pk} = #${r"{"}${property.propertyName}"}<#if property_has_next> AND </#if>
		</#list>
		</where>
	</delete>

	<#-- 更新操作 -->
	<update id="update" parameterType="map">
		UPDATE ${tableName}  
		<set>
			<#list columns as property>
			<if test="${property.propertyName} != null">
				${property.columnName} = #${r"{"}${property.propertyName}}<#if property_has_next>,</#if>
			</if>
			</#list>
		</set>
		<where>
			<#list pks as property>
			${property.pk} = #${r"{"}${property.propertyName}}<#if property_has_next> AND </#if>
			</#list>
		</where>
	</update>
</mapper>