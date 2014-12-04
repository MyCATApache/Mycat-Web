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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.hx.rainbow.common.web.session.ThreadConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

public class RainbowSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	  protected final Log logger = LogFactory.getLog(getClass());

	   
	  private RequestCache requestCache = new HttpSessionRequestCache();
	  
	  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	    throws ServletException, IOException
	  {
		  	RainbowUser user = (RainbowUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_USER, user);
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_USERNAME, user.getSessionData().get("name"));
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_LOGINID, user.getUsername());
		  	RainbowSession.web2Service(request);
		  	SavedRequest savedRequest = requestCache.getRequest(request, response);

	        if (savedRequest == null) {
	            super.onAuthenticationSuccess(request, response, authentication);

	            return;
	        }
	        String targetUrlParameter = getTargetUrlParameter();
	        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
	            requestCache.removeRequest(request, response);
	            super.onAuthenticationSuccess(request, response, authentication);

	            return;
	        }

	        clearAuthenticationAttributes(request);

	        // Use the DefaultSavedRequest URL
	        String targetUrl = savedRequest.getRedirectUrl();
	        logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
	        getRedirectStrategy().sendRedirect(request, response, targetUrl);
	  }
}