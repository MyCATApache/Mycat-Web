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
package org.hx.rainbow.common.web.session;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hx.rainbow.common.security.login.RainbowUser;
import org.hx.rainbow.common.util.PropertiesUtil;

public class RainbowSession implements java.io.Serializable {
	private static final long serialVersionUID = 8242424936268432231L;
	private static final String THREAD_LOACL_FILE = "threadLoacl";
	private static final List<String> SESSION_KEYS = Arrays.asList(ThreadConstants.RAINBOW_USER,ThreadConstants.RAINBOW_USERNAME,ThreadConstants.RAINBOW_LOGINID);

	private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

	private static Map<String, Object> getSessionData() {
		Map<String, Object> map = threadLocal.get();
		if (map == null) {
			map = new ConcurrentHashMap<String, Object>();
			threadLocal.set(map);
		}
		return map;
	}

	private static Object getProperty(String keyName) {
		Map<String, Object> map = getSessionData();
		return map.get(keyName);
	}

	private static void setProperty(String keyName,Object value) {
		if(keyName != null && value != null){
			Map<String, Object> map = getSessionData();
			map.put(keyName,value); 
		}
	}

	public static Object getUserName() {
		return (String) getProperty(ThreadConstants.RAINBOW_USERNAME);
	}

	public static String getLoginId() {
		return (String)getProperty(ThreadConstants.RAINBOW_LOGINID);
	}
	
	public static HttpServletRequest getHttpRequest() {
		return (HttpServletRequest)getProperty(ThreadConstants.RAINBOW_REQUEST);
	}

	public static String getClientIp() {
		return (String) getProperty(ThreadConstants.CONSTMER_IPADDRESS);
	}
	
	public static String getClientHost() {
		return (String) getProperty(ThreadConstants.CONSTMER_HOST);
	}
	
	public static String getClientPort() {
		return (String) getProperty(ThreadConstants.CONSTMER_PORT);
	}
	
	public static String getServiceIP() {
		return (String) getProperty(ThreadConstants.SERVICE_IPADDRESS);
	}
	
	public static String getServiceHost() {
		return (String) getProperty(ThreadConstants.SERVICE_HOST);
	}
	
	public static RainbowUser getUser() {
		return (RainbowUser) getProperty(ThreadConstants.RAINBOW_SESSION);
	}


	public static void web2Service(HttpServletRequest request) {
		if(request == null){
			return;
		}
		setProperty(ThreadConstants.CONSTMER_IPADDRESS,request.getRemoteAddr()); 
		setProperty(ThreadConstants.CONSTMER_HOST,request.getRemoteHost()); 
		setProperty(ThreadConstants.CONSTMER_PORT,request.getRemotePort()); 
		setProperty(ThreadConstants.SERVICE_IPADDRESS,request.getLocalAddr()); 
		setProperty(ThreadConstants.SERVICE_HOST,request.getLocalName()); 
		setProperty(ThreadConstants.RAINBOW_REQUEST,request);
		
		HttpSession session = request.getSession();
		
		RainbowUser rainbowUser = (RainbowUser)session.getAttribute(ThreadConstants.RAINBOW_USER);
		if(rainbowUser != null){
			setProperty(ThreadConstants.RAINBOW_SESSION,rainbowUser);
			setProperty(ThreadConstants.RAINBOW_LOGINID,rainbowUser.getUsername());
			setProperty(ThreadConstants.RAINBOW_USERNAME,rainbowUser.getSessionData().get("name"));
		
			String sessionKeys = (String)PropertiesUtil.getInstance().read(THREAD_LOACL_FILE).get(ThreadConstants.SESSION_KEYS);
			if (sessionKeys != null) {
				if (sessionKeys.equals("*")) {
					Enumeration<String> attrNames = session.getAttributeNames();
					while (attrNames.hasMoreElements()) {
						String attrName = (String) attrNames.nextElement();
						if(SESSION_KEYS.contains(attrName)){
							continue;
						}
						if(attrName != null){
							Object session_attr = session.getAttribute(attrName);
							if(session_attr != null){
								rainbowUser.getSessionData().put(attrName, session_attr);
							}
						}
					}
				} else {
					String[] s_sessionkey = StringUtils.split(sessionKeys, ",");
					for (int i = 0; i < s_sessionkey.length; i++) {
						if(s_sessionkey[i] != null){
							Object session_attr = session.getAttribute(s_sessionkey[i]);
							if(session_attr != null){
								rainbowUser.getSessionData().put(s_sessionkey[i],session_attr);
							}
						}
					}
				}
			}
		}
//	
//
//
//
//		Map<String, Object> inCookie = new ConcurrentHashMap<String, Object>();
//		String cookieKeys = (String)PropertiesUtil.get(ThreadConstants.resource_cookieKeys);
//
//		if (cookieKeys != null) {
//			Cookie[] cookies = request.getCookies();
//			if (cookies != null) {
//				if (cookieKeys.equals("*")) {
//					for (int i = 0; i < cookies.length; i++) {
//						Cookie cookie = cookies[i];
//						String cookieName = cookie.getName();
//						String cookieValue = cookie.getValue();
//						if(cookieName != null && cookieValue != null){
//							inCookie.put(cookieName, cookieValue);
//						}
//					}
//				} else {
//					cookieKeys = cookieKeys + ",";
//					for (int i = 0; i < cookies.length; i++) {
//						Cookie cookie = cookies[i];
//						String cookieName = cookie.getName();
//						if (cookieKeys.indexOf(cookieName + ",") > -1) {
//							String cookieValue = cookie.getValue();
//							if(cookieName != null && cookieValue != null){
//								inCookie.put(cookieName, cookieValue);
//							}
//						}
//					}
//				}
//			}
//			setProperty(ThreadConstants.IN_COOKIE, inCookie);
//		}

	}

	public static void service2Web(HttpServletRequest request) {
		if(request == null){
			return ;
		}
		request =  getHttpRequest();
	}
}