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

import org.hx.rainbow.common.dao.Dao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class RainbowUserDetailServiceImpl implements UserDetailsService{
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
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("loginId", userName);
		Map<String, Object> dataMap =  dao.get(NAMESPACE, STATEMENT,paramData);
		RainbowUser user = new RainbowUser((String)dataMap.get("password"), userName,dataMap);
	
		return user;
	}

}