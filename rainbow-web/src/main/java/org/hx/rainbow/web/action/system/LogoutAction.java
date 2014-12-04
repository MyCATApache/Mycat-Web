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
package org.hx.rainbow.web.action.system;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.cas.ServiceProperties;
import org.hx.rainbow.common.context.RainbowProperties;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logoutAction")
public class LogoutAction {
	private static final String CAS_LOGOUT = "cas.service.logout";
	private static final String CLIENT_SERVICE = "client.service";
	
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException{
		 HttpSession session = request.getSession(false);
         if (session != null) {
             session.invalidate();
         }
		String loginUrl = (String)RainbowProperties.getProperties(CAS_LOGOUT);
		String serviceUrl = (String)RainbowProperties.getProperties(CLIENT_SERVICE) + "/login";
		if(serviceUrl == null || serviceUrl.length() == 0){
			serviceUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/login";
		}
		ServiceProperties sp = (ServiceProperties)SpringApplicationContext.getBean("serviceProperties");
		String redirectUrl = CommonUtils.constructRedirectUrl(loginUrl, sp.getServiceParameter(), serviceUrl, false, false);
		response.sendRedirect(redirectUrl);
	}
}