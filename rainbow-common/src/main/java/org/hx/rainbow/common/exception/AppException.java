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
package org.hx.rainbow.common.exception;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = 8688953989589840707L;

	private Object[] args;
	private Object returnObj;

	public AppException(String msg) {
		super(msg);
	}

	public AppException(String msg, Object returnObj) {
		super(msg);
		this.returnObj = returnObj;
	}

	public AppException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AppException(String msg, Throwable cause, Object returnObj) {
		super(msg, cause);
		this.returnObj = returnObj;
	}

	public AppException(String msg, Object[] args) {
		super(msg);
		this.args = args;
	}

	public AppException(String msg, Object[] args, Object returnObj) {
		super(msg);
		this.args = args;
		this.returnObj = returnObj;
	}

	public AppException(String msg, Object[] args, Throwable cause) {
		super(msg, cause);
		this.args = args;
	}

	public AppException(String msg, Object[] args, Throwable cause,
			Object returnObj) {
		super(msg, cause);
		this.args = args;
		this.returnObj = returnObj;
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(Throwable cause, Object returnObj) {
		super(cause);
		this.returnObj = returnObj;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getReturnObj() {
		return this.returnObj;
	}

	public Throwable fillInStackTrace() {
		return this;
	}
}