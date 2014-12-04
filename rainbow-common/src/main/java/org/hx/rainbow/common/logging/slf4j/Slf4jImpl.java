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
package org.hx.rainbow.common.logging.slf4j;

import org.hx.rainbow.common.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jImpl implements Log {
	private Logger log;

	public Slf4jImpl(String clazz) {
		this.log = LoggerFactory.getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.log.isDebugEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		this.log.error(s, e);
	}



	@Override
	public void error(String s) {
		this.log.error(s);
	}



	@Override
	public void debug(String s) {
		this.debug(s);
	}



	@Override
	public void warn(String s) {
		this.warn(s);
		
	}

	@Override
	public boolean isInfoEnabled() {
		return this.log.isInfoEnabled();
	}

	@Override
	public void info(String s) {
		this.log.info(s);
		
	}

	@Override
	public void debug(String s, Throwable e) {
		this.log.debug(s,e);
	}

	@Override
	public boolean isWarnEnabled() {
		return	this.log.isWarnEnabled();
	}

	@Override
	public void warn(String s, Throwable e) {
		this.log.warn(s, e);
		
	}

}