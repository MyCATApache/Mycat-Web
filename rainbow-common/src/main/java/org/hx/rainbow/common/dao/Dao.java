/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.hx.rainbow.common.dao;

import java.util.List;
import java.util.Map;

public abstract interface Dao
{
  public static final String MYBATIS_DAO = "daoMybatis";
  
  public abstract List<Map<String,Object>> query(String namespace,String statement);
  
  public abstract List<Map<String,Object>> queryMycat(String namespace,String statement);
  
  public abstract List<Map<String,Object>> query(String namespace,String statement,int limit, int offset);

  public abstract List<Map<String,Object>> query(String namespace,String statement, Map<String,Object> paramData);

  public abstract List<Map<String,Object>> query(String namespace,String statement,Map<String,Object> paramData, int limit, int offset);

  public abstract int count(String namespace,String statement);

  public abstract int count(String namespace,String statement, Map<String,Object> paramData);

  public abstract Map<String,Object> get(String namespace,String statement, Map<String,Object> paramData);

  public abstract Map<String,Object> load(String namespace, String key, String value);

  public abstract void insert(String namespace,String statement, Map<String,Object> paramData);

  public abstract int update(String namespace,String statement, Map<String,Object> paramData);

  public abstract int delete(String namespace,String statement, Map<String,Object> paramData);
  
  public abstract String getSql(String namespace,String statement, Map<String,Object> paramData);

}