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
package org.hx.rainbow.common.logging.stdout;

import org.hx.rainbow.common.logging.Log;

public class StdOutImpl implements Log {

	public StdOutImpl(String clazz) {
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public void error(String s, Throwable e) {
		System.err.println(s);
		e.printStackTrace(System.err);
	}


	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String s) {
		System.out.println(s);
		
	}

	@Override
	public void debug(String s, Throwable t) {
		System.out.println(s);
		t.printStackTrace(System.out);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String s, Throwable t) {
		System.out.println(s);
		t.printStackTrace(System.out);
	}

	@Override
	public void error(String s) {
		System.out.println(s);
		
	}

	@Override
	public void debug(String s) {
		System.out.println(s);
		
	}

	@Override
	public void warn(String s) {
		System.out.println(s);
		
	}
}