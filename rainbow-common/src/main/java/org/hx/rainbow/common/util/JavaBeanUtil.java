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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.hx.rainbow.common.exception.AppException;


/**
 * 操作javaBean工具类
 * 
 * @author hx
 * 
 */
public  class JavaBeanUtil {

	
	
	/**
	 * 高性能map to bean
	 * @param bean 转换的bean对象
	 * @param map 
	 * @param allowEmptyString string类型中是否允许""赋值
	 * @author huangxin
	 */
	public  static void map2bean(Object bean, Map<String,Object> map,boolean allowEmptyString){
		map2bean(bean,map,allowEmptyString,DateUtil.DEFAULT_DATE_PATTERN);
	}
	
	/**
	 * 高性能map to bean
	 * @param bean 转换的bean对象
	 * @param map 
	 * @param allowEmptyString string类型中是否允许""赋值
	 * @param dataFormat 如果map中有日期转换为bean中String,需要转为相应格式 如：yyyy-MM-dd HH:mm:ss 
	 * @author huangxin
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  static void map2bean(Object bean, Map<String,Object> map,boolean allowEmptyString,String dataFormat){
		if(bean == null || map == null){
			return ;
		}
		Class beanClass = bean.getClass();
		if(CglibUitl.getInstance().isCglib(beanClass)){
			BeanMap beanMap = BeanMap.create(bean);	
			beanClass =  beanClass.getSuperclass();
			Map<String,Class> filedMap = getFileds(beanClass);
			for(Map.Entry<String,Object> entry : map.entrySet()){
				try {
					Object value = entry.getValue();
					String key = entry.getKey();
					if(value == null){
						continue;
					}
					if(value.toString().trim().length() == 0 && allowEmptyString){
						continue;
	    			}
					if(filedMap.containsKey(key)){
						Class clazz = filedMap.get(key);
						changeObject(beanMap,key,value,clazz,dataFormat);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			bean = beanMap.getBean();
			return;
		}
		
		 Method[] methods = beanClass.getMethods();
		 for (Method method : methods){ 
	            try 
	            { 
	                if (method.getName().startsWith("set")) 
	                { 
	                    String field = method.getName(); 
	                    field = field.substring(field.indexOf("set") + 3); 
	                    field = field.toLowerCase().charAt(0) + field.substring(1); 
	                   
	                	Object o = map.get(field);
	        			if(o == null){
	        				continue;
	        			} 
	        			if(o instanceof String){
		        			if(o.toString().trim().length() == 0 && allowEmptyString){
		        				continue; 
		            		}	
		        			method.invoke(bean, new Object[]{o}); 
	        			}else{
	        				method.invoke(bean, new Object[]{o}); 
	        			}
	        		}
	            } 
	            catch (Exception e) 
	            { 
	            	e.printStackTrace();
	            } 
	        }
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void changeObject(Map<String,Object> map,String key,Object value,Class clazz,String dataFormat)throws ParseException{
		if(value instanceof String){
			String valueStr = (String)value;
			if(clazz.isEnum()){
				map.put(key, Enum.valueOf(clazz, valueStr));
			}else if(clazz == Date.class){
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_PATTERN);
				Date date = sdf.parse(valueStr);
				map.put(key,date);
			}else if(clazz == Integer.class){
				map.put(key,Integer.valueOf(valueStr));
			}else if(clazz == BigDecimal.class){
				map.put(key,new BigDecimal(valueStr));
			}else if(clazz == Boolean.class){
				map.put(key,new Boolean(valueStr));
			}else if(clazz == Number.class){
				map.put(key,new Integer(valueStr));
			}else if (clazz == int.class){
				map.put(key, Integer.parseInt(valueStr));
			}else{
				map.put(key, valueStr);
			}
		}else if(value instanceof Integer){
			Integer valueInt = (Integer)value;
			if(clazz == String.class){
				map.put(key,valueInt.toString());
			}else if(clazz == Date.class){
				map.put(key,new Date(valueInt));
			}else{
				map.put(key,valueInt);
			}
		}else if(value instanceof Boolean){
			Boolean valueBoolean = (Boolean)value;
			if(clazz == String.class){
				map.put(key,valueBoolean.toString());
			}else {
				map.put(key,valueBoolean);
			}
		}else if(value instanceof Date){
			Date valueDate = (Date)value;
			if(clazz == String.class){
				SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
				map.put(key,sdf.format(valueDate));
			}else{
				map.put(key,valueDate);
			}
		}else if(value instanceof BigDecimal){
			BigDecimal valueBigDecimal = (BigDecimal)value;
			if(clazz == String.class){
				map.put(key,valueBigDecimal.toPlainString());
			}else if(clazz == Integer.class){
				map.put(key,valueBigDecimal.toBigInteger());
			}else{
				map.put(key,valueBigDecimal);
			}
		}else{
			map.put(key,value);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private static Map<String,Class> getFileds(Class clazz) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		Map<String,Class> map = new HashMap<String,Class>();
		PropertyDescriptor[] pr = beanInfo.getPropertyDescriptors();
		for (int i = 1; i < pr.length; i++) {
			map.put(pr[i].getName(), pr[i].getPropertyType());
		}
		Field[] field = clazz.getDeclaredFields();
		for (int i = 1; i < field.length; i++) {
			map.put(field[i].getName(), field[i].getType());
		}
		return map;
	}
	
	/**
	 * bean to map 
	 * @param bean 转换的bean对象
	 * @param map 
	 * @return map 如果bean为null 返回null
	 * @author huangxin
	 */
	@SuppressWarnings("unchecked")
	public  static Map<String,Object> bean2Map(Map<String,Object> map, Object bean){
		if(bean == null){
			return null;
		}
		if(map == null){
			map = new HashMap<String,Object>();
		}
		//判断是否是cglib代理对象
		if(CglibUitl.getInstance().isCglib(bean.getClass())){
			Map<String,Object> maps = BeanMap.create(bean);
			for(Entry<String,Object> entry :  maps.entrySet()){
				if(entry.getValue() == null){
					continue;
				}
				if(entry.getKey().contains("callbacks")){
					continue;
				}
				if(entry.getKey().contains("bstate")){
					continue;
				}
				map.put(entry.getKey(), entry.getValue());
				
			}
			return map;
		}
		
		Method[] methods = bean.getClass().getMethods();
		 for (Method method : methods){ 
	            try 
	            { 
	                if (method.getName().startsWith("get")) 
	                { 
	                    String field = method.getName(); 
	                    field = field.substring(field.indexOf("get") + 3); 
	                    field = field.toLowerCase().charAt(0) + field.substring(1); 
	                   
	                  Object o =  method.invoke(bean, (Object[])null); 
	        			if(o == null){
	        				continue;
	        			} 
	        			if(o instanceof Date){
	    					SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_PATTERN);
	    					map.put(field,sdf.format(o));
	        			}else if(o instanceof String){
	        				String str = (String)o;
//	        				if(Pattern.compile("[<>]+").matcher(str).find()){
//	        					str = str.replaceAll("<", "&lt").replaceAll(">",  "&gt");
//	        				}
	    					map.put(field,str);
	    				}else{
	    					map.put(field,o);
	    				}
	        		}
	            } 
	            catch (Exception e) 
	            { 
	            	e.printStackTrace();
	            } 
	        }
		return map;
	}

	/**
	 * 将一个对象的属性值取出来放置到Map中。Map的Key为对象属性名称
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	public static Map getProperties(Object bean) {
		if (bean == null) {
			return null;
		}

		Map dataMap = new HashMap();
		try {
			PropertyDescriptor origDescriptors[] = PropertyUtils
					.getPropertyDescriptors(bean);

			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (name.equals("class")) {
					continue;
				}

				if (PropertyUtils.isReadable(bean, name)) {
					Object obj = PropertyUtils.getProperty(bean, name);
					if (obj == null) {
						continue;
					}
					obj = convertValue(origDescriptors[i], obj);
					dataMap.put(name, obj);
				}
			}// for end
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e.getMessage());
		}
		return dataMap;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getMapList(List beanList) {
		if (beanList == null) {
			return null;
		}

		ArrayList lstMap = new ArrayList();
		Iterator iter;
		try {
			iter=beanList.iterator();
			while(iter.hasNext()){
				Object obj=iter.next();
				Map map=getProperties(obj);
				lstMap.add(map);
			}
		} catch (AppException e) {
			e.printStackTrace();
			throw e;
		}
		return lstMap;
	}
	
	private static Object convertValue(PropertyDescriptor origDescriptor,
			Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj.toString().trim().length() == 0) {
			return null;
		}
		if (origDescriptor.getPropertyType() == java.util.Date.class) {
			//同一个时间，第一次从界面层传过来时，obj为String类型;转化后为Date类型
			 if (obj instanceof Date) {
				 return obj;
			}else{
				try {
					//修改 时间转换时会把带时分秒的截掉的问题 2012-5-10 张慧峰
					if(obj.toString().length()>10)
						obj = DateUtil.toDateTime(obj.toString());
					else
						obj = DateUtil.toDate(obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
					throw new AppException(e.getMessage());
				}
			}
		}
		return obj;
	}
	
	/**
	 * 高性能的Bean copy
	 * @param fromBean
	 * @param toBean
	 * @author huangxin
	 */
	public static void beanCopy(Object fromBean ,Object toBean){
		if (fromBean == null || toBean==null) {
			return;
		}
		BeanCopier b = BeanCopier.create(fromBean.getClass(), toBean.getClass(), false);
		b.copy(fromBean, toBean, null);
	}
	
	/**
	 * 高性能的Bean copy
	 * @param fromBean
	 * @param toBean
	 * @param isNotNull 为true是fromBean的null不复制给toBean
	 * @author huangxin
	 */
	public static void beanCopy(Object fromBean ,Object toBean,boolean isNotNull){
		if (fromBean == null || toBean==null) {
			return;
		}
		if(!isNotNull){
			beanCopy(fromBean,toBean);
			return;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		bean2Map(map, fromBean); 
		map2bean(toBean, map, false);
	}
	
	/**
	 * 将一个bean的属性复制到另一个bean的同名属性中
	 * zhf 2012-5-14 [修改] 用BeanUtils.copyProperties方法copy属性出错问题
	 * @param fromBean
	 * @param toBean
	 */
	public static  void copyProperties(Object fromBean ,Object toBean){
		if (fromBean == null||toBean==null) {
			return;
		}
		try {
//			BeanUtils.copyProperties(toBean, fromBean);
			PropertyDescriptor origDescriptors[] = PropertyUtils
					.getPropertyDescriptors(toBean);

			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (name.equals("class")) {
					continue;
				}

				//if (PropertyUtils.isReadable(fromBean, name)||PropertyUtils.isWriteable(toBean, name)) {
				if (PropertyUtils.isReadable(fromBean, name)&&PropertyUtils.isWriteable(toBean, name)) {
					Object obj = PropertyUtils.getProperty(fromBean, name);
					if (obj == null) {
						continue;
					}
					obj = convertValue(origDescriptors[i], obj);
					BeanUtils.copyProperty(toBean, name, obj);
				}
			}// for end
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getEntityList(List mapList,Class clazz) {
       if (mapList == null) {
           return null;
       }
       ArrayList ListEntity = new ArrayList();
       Iterator iter;
       try {
           iter = mapList.iterator();
           while (iter.hasNext()) {
              Map map = (Map) iter.next();
              Object obj = clazz.newInstance();
              map2bean(obj, map, false);
              ListEntity.add(obj);
           }
       } catch (Exception e) {
           e.printStackTrace();
           throw new AppException(e.getMessage());
       }
       return ListEntity;
    }
	
	public static void main(String[] args) {
		String a = "123123123";
		Pattern p=Pattern.compile("[<>\"]+");
		Matcher m=p.matcher(a);
		System.out.println(m.find());
	}
}