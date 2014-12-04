<?xml version="1.0" encoding="UTF-8"?>
<!--
 -  
 - Licensed under the Apache License, Version 2.0 (the "License");
 - you may not use this file except in compliance with the License.
 - You may obtain a copy of the License at
 -  
 -      http://www.apache.org/licenses/LICENSE-2.0
 -  
 - Unless required by applicable law or agreed to in writing, software
 - distributed under the License is distributed on an "AS IS" BASIS,
 - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 - See the License for the specific language governing permissions and
 - limitations under the License.
-->
<!DOCTYPE mycat:rule SYSTEM "rule.dtd">
<mycat:rule xmlns:mycat="http://org.opencloudb/">

  <#list tableruleService as rule >
  <tableRule name="${rule.name}">
    <rule>
      <columns>${rule.columns}</columns>
      <algorithm>${rule.algorithm}</algorithm>
    </rule>
  </tableRule>
  </#list>
  
  <#list functionService as fn >
  <function name="${fn.name}" class="${fn.class}">
  	<#list fn.property as pop>
    <property name="${pop.paramKey}">${pop.paramValue}</property>
    </#list>
  </function>
  </#list>
  
</mycat:rule>
