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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 日期操作工具类
 * 
 * @author hx
 * 
 */
public class DateUtil {
	/**
	 * 默认日期格式：yyyy-MM-dd
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * 默认时间格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 默认时间戳格式，到毫秒 yyyy-MM-dd HH:mm:ss SSS
	 */
	public static final String DEFAULT_DATEDETAIL_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";

	/**
	 * 1天折算成毫秒数
	 */
	public static final long MILLIS_A_DAY = 24 * 3600 * 1000;
	
	private static HashMap<String,Object> parsers = new HashMap<String,Object>();

	private static SimpleDateFormat getDateParser(String pattern) {
		Object parser = parsers.get(pattern);
		if (parser == null) {
			parser = new SimpleDateFormat(pattern);
			parsers.put(pattern, parser);
		}
		return (SimpleDateFormat) parser;
	}
	/**
	 * 取得系统当前年份
	 * @return
	 */
	public static int currentYear()
	{
		java.util.Calendar c = java.util.Calendar.getInstance();
		return c.get(java.util.Calendar.YEAR);
	}
	/**
	 * 取得当前系统日期
	 * 
	 * @return
	 */
	public static java.util.Date currentDate() {
		return new java.util.Date();
	}

	/**
	 * 取得系统当前日期，返回默认日期格式的字符串。
	 * 
	 * @param strFormat
	 * @return
	 */
	public static String nowDate(String strFormat) {
		java.util.Date date = new java.util.Date();
		return getDateParser(strFormat).format(date);
	}

	/**
	 * 取得当前系统时间戳
	 * 
	 * @return
	 */
	public static Timestamp currentTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 将日期字符串转换为java.util.Date对象
	 * 
	 * @param dateString
	 * @param pattern
	 *            日期格式
	 * @return
	 * @throws Exception
	 */
	public static java.util.Date toDate(String dateString, String pattern)
			throws Exception {
		return getDateParser(pattern).parse(dateString);
	}

	/**
	 * 将日期字符串转换为java.util.Date对象，使用默认日期格式
	 * 
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
	public static java.util.Date toDate(String dateString) throws Exception {
		return getDateParser(DEFAULT_DATE_PATTERN).parse(dateString);
	}

	/**
	 * 将时间字符串转换为java.util.Date对象
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
	public static java.util.Date toDateTime(String dateString) throws Exception {
		return getDateParser(DEFAULT_DATETIME_PATTERN).parse(dateString);
	}

	/**
	 * 将java.util.Date对象转换为字符串
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toDateString(java.util.Date date, String pattern) {
		return getDateParser(pattern).format(date);
	}

	/**
	 * 将java.util.Date对象转换为字符串，使用默认日期格式
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toDateString(java.util.Date date) {
		return getDateParser(DEFAULT_DATE_PATTERN).format(date);
	}

	/**
	 * 将java.util.Date对象转换为时间字符串，使用默认日期格式
	 * @param date
	 * @return
	 */
	public static String toDateTimeString(java.util.Date date) {
		return getDateParser(DEFAULT_DATETIME_PATTERN).format(date);
	}
	
	
	/**
	 * 日期相减
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date diffDate(java.util.Date date, int day) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) - ((long) day) * MILLIS_A_DAY );
		return c.getTime();
	}
	
	/**
	 * 返回毫秒
	 * 
	 * @param date
	 *            日期
	 * @return 返回毫秒
	 * @author doumingjun create 2007-04-07
	 */
	public static long getMillis(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}
	
	/**
	 * 日期相加
	 * 
	 * @param date
	 *            日期
	 * @param day
	 *            天数
	 * @return 返回相加后的日期
	 * @author doumingjun create 2007-04-07
	 */
	public static java.util.Date addDate(java.util.Date date, int day) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		
		c.setTimeInMillis(getMillis(date) + ((long) day) * MILLIS_A_DAY);
		return c.getTime();
	}
	
	/**
	 * 日期增加年数
	 * @param date
	 * @param year
	 * @return
	 * @author zhf
	 */
	public static Date addYear(Date date , int year){
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.YEAR, year);
		return calender.getTime();
	}
	
	/**
	 * 日期增加月数
	 * @param date
	 * @param year
	 * @return
	 * @author zhf
	 */
	public static Date addMonth(Date date , int month){
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.MONTH, month);
		return calender.getTime();
	}
	
	/**
	 *  根据季度获得相应的月份
	 *  
	 *  @param quarters 季度
	 *  
	 *  @return 返回相应的月份
	 */
	public static String getMonth(String quarters) {
		String month;
		int m = Integer.parseInt(quarters);
		m = m * 3 - 2;
		if (m > 0 && m < 10) {
			month = "0" + String.valueOf(m);
		} else {
			month = String.valueOf(m);
		}
		return month;
	}
	
	/**
	 *  根据月份获得相应的季度
	 *  
	 *  @param month 月份
	 *  
	 *  @return 返回相应的季度
	 */
	public static String getQuarters(String month) {
		String quarters = null;
		int m = Integer.parseInt(month);
		if (m == 1 || m == 2 || m == 3) {
			quarters = "1";
		}
		if (m == 4 || m == 5 || m == 6) {
			quarters = "2";
		}
		if (m == 7 || m == 8 || m == 9) {
			quarters = "3";
		}
		if (m == 10 || m == 11 || m == 12) {
			quarters = "4";
		}
		return quarters;
	}
	
	/**
	 * 获取日期所在星期的第一天，这里设置第一天为星期日
	 * @param datestr
	 * @return
	 */
	public static String getFirstDateOfWeek(String datestr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=sdf.parse(datestr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//这里设置从周日开始
			return sdf.format(cal.getTime());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取日期所在当年的第几周
	 * @param datestr
	 * @return
	 */
	public static int getWeekOfYear(String datestr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=sdf.parse(datestr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.WEEK_OF_YEAR);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	public static String getWeekday(String datestr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=sdf.parse(datestr);
			switch (date.getDay()) {
			case 1:
				return "星期一";
			case 2:
				return "星期二";
			case 3:
				return "星期三";
			case 4:
				return "星期四";
			case 5:
				return "星期五";
			case 6:
				return "星期六";
			default:
				return "星期天";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	
	}
	
	public static Date getDate(Object object) {
		Date date = null;

		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");
			if (object instanceof String) {
				try {
					date = format.parse((String) object);
				} catch (Exception e) {
					try {
						date = format2.parse((String) object);
					} catch (Exception ee) {
						date = format3.parse((String) object);
					}
				}
			} else if (object instanceof java.util.Date) {
				date = (Date) object;
			} else if (object instanceof Timestamp) {
				date = (Date) object;
			} else {
				date = new java.util.Date();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
}