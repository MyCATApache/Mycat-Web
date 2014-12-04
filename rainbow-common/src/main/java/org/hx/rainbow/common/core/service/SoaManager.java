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
package org.hx.rainbow.common.core.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.exception.SysException;

public class SoaManager {

	private SoaInvoker soaInvoker;
	
	private volatile static SoaManager soaManager;
	
	private SoaManager(){}
	
	public static SoaManager getInstance(){
		if(soaManager == null){
			synchronized (SoaManager.class) {
				if(soaManager == null){
					soaManager = new SoaManager();
				}
			}
		}
		return soaManager;
	}
	

	public  RainbowContext invoke(RainbowContext context) {
		return callTx(context, 0);
	}

	public  RainbowContext call(RainbowContext context) {

		SoaInvoker SoaInvoker = (SoaInvoker) SpringApplicationContext
				.getBean("soaInvoker");
		return SoaInvoker.invoke(context);
	}

	public  RainbowContext invokeNoTx(RainbowContext context) {
		SoaInvoker SoaInvoker = (SoaInvoker) SpringApplicationContext
				.getBean("soaInvoker");
		return SoaInvoker.invoke(context);
	}

	public  RainbowContext callNoTx(RainbowContext context) {
		return callTx(context, 4);
	}

	public  RainbowContext callNewTx(RainbowContext context) {
		return callTx(context, 3);
	}

	private  RainbowContext callTx(RainbowContext context, int txType) {
		
		if (context == null) {
			throw new SysException("rainbowSoa: Service invoker is error");
		}
		if (context.getService() == null) {
			throw new SysException("rainbowSoa: Service is null !!");
		}
		if(context.getMethod() == null){
			throw new SysException("rainbowSoa: Service's method is null !!");
		}
		if(this.soaInvoker == null){
			this.soaInvoker = (SoaInvoker)SpringApplicationContext.getBean("soaInvoker");
		}
		
		RainbowContext info = new RainbowContext();
		try {
			context.addAttr("transactionType", Integer.valueOf(txType));
			info = this.soaInvoker.invoke(context);
		} catch (Exception e) {
			if ((e instanceof AppException)) {
				throw ((AppException) e);
			}else{
				throw new SysException("rainbowSoa: Service invoker is error!"+e.getMessage());
			}
		}

		return info;
	}

	public  SoaInvoker getSoaInvoker() {
		return soaInvoker;
	}

	public void setSoaInvoker(SoaInvoker soaInvoker) {
		this.soaInvoker = soaInvoker;
	}
}