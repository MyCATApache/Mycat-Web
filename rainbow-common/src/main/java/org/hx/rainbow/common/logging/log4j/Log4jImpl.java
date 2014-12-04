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
package org.hx.rainbow.common.logging.log4j;


import org.apache.log4j.Logger;
import org.hx.rainbow.common.logging.Log;

public class Log4jImpl implements Log {
	private Logger log;

	public Log4jImpl(String clazz) {
		this.log = Logger.getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return this.log.isDebugEnabled();
	}

	@Override
	public void error(String s, Throwable t) {
		this.log.error(s, t);
	}

	@Override
	public void error(String s) {
		this.log.error(s);
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
	public void debug(String s) {
		this.log.debug(s);
	}

	@Override
	public void debug(String s, Throwable t) {
		this.log.debug(s, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return false;
	}

	@Override
	public void warn(String s) {
		 this.log.warn(s);
	}

	@Override
	public void warn(String s, Throwable t) {
		this.log.warn(s, t);
	}

	


}