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

import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

import org.hx.rainbow.common.ddd.model.Entity;


public class CglibUitl {
	private static final String CGLIB = "$$";
	
	private volatile static CglibUitl cglibUitl = null;
	private CglibUitl(){
		
	}
	public static CglibUitl getInstance(){
		if(cglibUitl == null){
			synchronized (CglibUitl.class) {
				if(cglibUitl == null){
					cglibUitl = new CglibUitl();
				}
			}
		}
		return cglibUitl;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isCglib(Class clazz){
		if(clazz == null){
			return false;
		}
		return clazz.getSimpleName().contains(CGLIB);
	}
	
	/**
	 * 还原CGLIB对象
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object restore(Object object){
		if(object == null){
			return null;
		}
		try {
				Map<String,Object> map = BeanMap.create(object);
				Map<String,Object> newmap =  new HashMap<String,Object>();
				for(Map.Entry<String,Object> entry : map.entrySet()){
					if(entry.getValue() == null){
						continue;
					}
					if(entry.getValue() instanceof Entity && isCglib(entry.getValue().getClass())){
						Object newObject = entry.getValue().getClass().getSuperclass().newInstance();
						JavaBeanUtil.beanCopy(entry.getValue(), newObject);
						newmap.put(entry.getKey(), newObject);
						restore(newObject);
					}
//					if(entry.getValue() instanceof List){
//						List<Object> list = (List<Object>)entry.getValue();
//						List<Object> newlist = new ArrayList<Object>();
//						for(Object ob : list){
//							if(isCglib(ob.getClass())){
//								Object newObject = ob.getClass().getSuperclass().newInstance();
//								JavaBeanUtil.beanCopy(ob, newObject);
//								newlist.add(newObject);
//							}
//						} 
//						newmap.put(entry.getKey(), newlist);
//						restore(entry.getValue());
//					}
				}
				map.putAll(newmap);
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		return changeSelf(object);
	}
	
	private Object changeSelf(Object object){
		if(object == null){
			return null;
		}
		try{
			if(isCglib(object.getClass())){
				Object newObject = object.getClass().getSuperclass().newInstance();
				JavaBeanUtil.beanCopy(object, newObject);
				object = newObject;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}