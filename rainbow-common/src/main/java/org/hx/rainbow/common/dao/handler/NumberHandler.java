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
package org.hx.rainbow.common.dao.handler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

@SuppressWarnings("rawtypes")
public class NumberHandler implements TypeHandler {
	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			JdbcType jdbcType) throws SQLException {
		if(parameter != null && !"".equals(parameter)){
			if(parameter instanceof String){
				String number =  (String)parameter;
				ps.setBigDecimal(i,new BigDecimal(number));
			}else if(parameter instanceof BigDecimal){
				ps.setBigDecimal(i, (BigDecimal)parameter);
			}else if(parameter instanceof Integer){
				ps.setInt(i, (Integer)parameter);
			}else if(parameter instanceof Double){
				ps.setDouble(i, (Double)parameter);
			}else if(parameter instanceof Float){
				ps.setFloat(i, (Float)parameter);
			}else if(parameter instanceof Long){
				ps.setLong(i, (Long)parameter);
			}
		}else{
			ps.setBigDecimal(i,null);
		}

	}

	@Override
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		return rs.getObject(columnName);
	}

	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getObject(columnIndex);
	}
	
}