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
package org.hx.rainbow.common.security.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hx.rainbow.common.dao.Dao;
import org.hx.rainbow.common.security.md5.Md5;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class RainbowLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	public static final String USERNAME = "userName";
	public static final String PASSWORD = "password";
	public static final String VALIDATECODE = "kaptchafield";
	private static final String NAMESPACE = "SYSUSER";
	private static final String STATEMENT = "query";
	
	private Dao dao;
	

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
	
		String username = obtainUsername(request);
/*		//检测验证码
		if(!"admin".equals(username)){
			checkValidateCode(request);
		}*/
		String password = Md5.getInstance().encrypt(obtainPassword(request));
		//验证用户账号与密码是否对应
		username = username.trim();
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("loginId", username);
		paramData.put("password",password);
		Map<String,Object> userData = this.dao.get(NAMESPACE, STATEMENT, paramData);
		
		
		if(userData == null || userData.size() == 0) {
			throw new AuthenticationServiceException("?error=2"); 
		}
		
		if(!"0".equals((String)userData.get("aliveFlag"))){
			throw new AuthenticationServiceException("?error=3"); 
		};
		
		//UsernamePasswordAuthenticationToken实现 Authentication
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
	}
	
/*	protected void checkValidateCode(HttpServletRequest request) { 
		HttpSession session = request.getSession();
		String kaptchaReceived = request.getParameter(VALIDATECODE);
	    if (kaptchaReceived == null || kaptchaReceived.isEmpty() || !validateCode(session).equals(kaptchaReceived)) {  
	        throw new AuthenticationServiceException("?error=0");  
	    }  
	}*/
	
/*	protected String validateCode(HttpSession session) {
		Object obj = session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		return null == obj ? "" : obj.toString();
	}*/

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		Object obj = request.getParameter(USERNAME);
		return null == obj ? "" : obj.toString();
	}
	

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		Object obj = request.getParameter(PASSWORD);
		return null == obj ? "" : obj.toString();
	}
	
	
}