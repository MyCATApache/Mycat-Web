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

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

@SuppressWarnings("rawtypes")
public class DateHandler implements TypeHandler {
	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			JdbcType jdbcType) throws SQLException {
		if(parameter != null && !"".equals(parameter)){
			if(parameter instanceof String){
				String date = (String)parameter;
				int length = date.length();
				if(length == 8){
					if(isNumeric(date)){
						date = date.substring(0, 4)+"-"+date.substring(4, 6) + "-" + date.substring(6, 8);
					}
					ps.setDate(i, Date.valueOf(date));
				}else{
					date = date.replaceAll("(\\d{4}).{0,1}(\\d{2}).{0,1}(\\d{2})(.*)", "$1-$2-$3$4");
					if(length == 10){
						ps.setDate(i, Date.valueOf(date));
					}else{
						ps.setTimestamp(i, Timestamp.valueOf(date));
					}
				}
			}else if(parameter instanceof java.util.Date){
				java.util.Date date = (java.util.Date)parameter;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = sdf.format(date);
				if(dateStr.indexOf("00:00:00") != -1){
					ps.setDate(i, new Date(((java.util.Date)parameter).getTime()));
				}else{
					ps.setTimestamp(i, new Timestamp(((java.util.Date)parameter).getTime()));
				}
			}else if(parameter instanceof Date){
				ps.setDate(i, (Date)parameter);
			}else if(parameter instanceof Timestamp){
				ps.setTimestamp(i, (Timestamp)parameter);
			}
		}else{
			ps.setTimestamp(i, null);
		}
	}

	@Override
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		Object o =  rs.getObject(columnName);
		if(o instanceof Date){
			return (Date)o;
		}else if(o instanceof Timestamp){
			return (Timestamp)o;
		}else{
			return o;
		}
	}
	
	private static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 

	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		Object o =  rs.getObject(columnIndex);
		if(o instanceof Date){
			return (Date)o;
		}else if(o instanceof Timestamp){
			return (Timestamp)o;
		}else{
			return o;
		}
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		Object o =  cs.getObject(columnIndex);
		if(o instanceof Date){
			return (Date)o;
		}else if(o instanceof Timestamp){
			return (Timestamp)o;
		}else{
			return o;
		}
	}
}