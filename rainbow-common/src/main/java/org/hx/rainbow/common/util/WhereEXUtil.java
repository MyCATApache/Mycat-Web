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
package org.hx.rainbow.common.util;

import java.util.Map;

public class WhereEXUtil {

	private static WhereEXUtil whereexUtil;

	private WhereEXUtil() {
	};

	public static WhereEXUtil getInstance() {
		if (whereexUtil == null) {
			synchronized (WhereEXUtil.class) {
				if (whereexUtil == null) {
					whereexUtil = new WhereEXUtil();
				}
			}
		}
		return whereexUtil;
	}

	public String map2Whereex(Map<String, Object> attr) {
		if(attr.isEmpty()){
			return null;
		}
		String fields = (String) attr.get("field");
		String symbols = (String) attr.get("symbol");
		String values = (String) attr.get("value");
		String valueType = (String) attr.get("valueType");
		if(isEmpty(fields) || isEmpty(symbols)){
			return null;
		}
		String[] fieldsArr = fields.split(",");
		String[] symbolsArr = symbols.split(",");
		String[] valuesArr = values.split(",");
		int count = 0;
		StringBuffer sb = new StringBuffer(" 1=1");
		for (int i = 0; i < fieldsArr.length; i++) {
			sb.append(" and ").append(fieldsArr[i]).append(" ");
			String symbol = symbolsArr[i];
			if(symbol.equals("bt")){
				sb.append("between '").append(valuesArr[i + count]).append("'");
				count++;
				sb.append(" and '").append(valuesArr[i + count]).append("'");
			}else{
				sb.append(symbol).append(" ");
				if(symbol.equals("is null")){
					count--;
				}else if(symbol.equals("like")){
					sb.append("'%").append(valuesArr[i + count]).append("%'");
				}else{
					sb.append("'").append(valuesArr[i + count]).append("'");
				}
			}
		}
		return sb.toString();
	}
	
	private boolean isEmpty(String str){
		if(str == null || str.trim().isEmpty()){
			return true;
		}
		return false;
	}
}