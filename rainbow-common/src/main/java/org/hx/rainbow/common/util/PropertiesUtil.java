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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 读取.properties配置文件的内容至Map中。
 * @author huangxin
 *
 */
public class PropertiesUtil {


	protected final static Log logger = LogFactory.getLog(PropertiesUtil.class);
	
	public static Map<String,Object> map = new HashMap<String,Object>();
	
	private static PropertiesUtil propertiesUtil = null;
	private PropertiesUtil(){
		
	}
	
	public static PropertiesUtil getInstance(){
		if(propertiesUtil == null){
			synchronized (PropertiesUtil.class) {
				if(propertiesUtil == null){
					propertiesUtil =  new PropertiesUtil();
				}
			}
		}
		return propertiesUtil;
	}
	

	
	/**
	 * 读取.properties配置文件的内容至Map中
	 * @param propertiesFile
	 * @return
	 */
	public  Map<String,Object> read(String propertiesFile) {
		
		@SuppressWarnings("unchecked")
		Map<String,Object> maps = (Map<String, Object>) map.get(propertiesFile);
		if(maps == null){
			maps = new HashMap<String,Object>();
			ResourceBundle rb = ResourceBundle.getBundle(propertiesFile);
			Enumeration<String> enu = rb.getKeys();
			while (enu.hasMoreElements()) {
				String obj = enu.nextElement();
				Object objv = rb.getObject(obj);
				maps.put(obj, objv);
			}
			map.put(propertiesFile, maps);
		}
		return maps;
	}
	


}