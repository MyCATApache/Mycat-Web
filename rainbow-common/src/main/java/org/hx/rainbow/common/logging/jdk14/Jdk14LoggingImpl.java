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
package org.hx.rainbow.common.logging.jdk14;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hx.rainbow.common.logging.Log;

public class Jdk14LoggingImpl implements Log {
	private Logger log;

	public Jdk14LoggingImpl(String clazz) {
		this.log = Logger.getLogger(clazz);
	}


	@Override
	public boolean isInfoEnabled() {
		return this.log.isLoggable(Level.INFO);
	}

	@Override
	public void info(String s) {
		this.log.info(s);
	}

	@Override
	public void debug(String s, Throwable t) {
		this.log.log(Level.FINE, s, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return this.log.isLoggable(Level.WARNING);
	}

	@Override
	public void warn(String s, Throwable t) {
		this.log.log(Level.WARNING, s, t);
	}


	@Override
	public boolean isDebugEnabled() {
		return this.log.isLoggable(Level.FINE);
	}


	@Override
	public void error(String s, Throwable t) {
		this.log.log(Level.SEVERE, s, t);
	}


	@Override
	public void error(String s) {
		this.log.log(Level.SEVERE, s);
	}


	@Override
	public void debug(String s) {
		this.log.log(Level.FINE, s);
	}


	@Override
	public void warn(String s) {
		this.log.log(Level.WARNING, s);
	}
}