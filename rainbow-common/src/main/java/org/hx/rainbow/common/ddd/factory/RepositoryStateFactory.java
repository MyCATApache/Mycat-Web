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
package org.hx.rainbow.common.ddd.factory;

import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.base.RepositoryState;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;
import org.hx.rainbow.common.util.CglibUitl;

/**
 * 仓储状态工厂
 * @author huangxin
 *
 */
public class RepositoryStateFactory {
	
	private volatile static RepositoryStateFactory repositorystatefactory = null;

	private RepositoryStateFactory() {
	}

	public static RepositoryStateFactory getInstance() {
		if (repositorystatefactory == null) {
			synchronized (RepositoryStateFactory.class) {
				if (repositorystatefactory == null) {
					repositorystatefactory = new RepositoryStateFactory();
				}
			}
		}
		return repositorystatefactory;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RepositoryState> T  getRepositoryState(Entity<?> e, IEntityState state){
		if(e == null || state == null ){
			return null;
		}
		T t =  null;
		Class clazz = e.getClass();
		if(CglibUitl.getInstance().isCglib(e.getClass())){
			clazz = e.getClass().getSuperclass();
		}
		if(clazz.isAnnotationPresent(Aggergation.class)){
			Aggergation aggergation = (Aggergation) clazz.getAnnotation(Aggergation.class);
			Class root = aggergation.root();
			if(root != null){
				clazz = root;
			}
		}
		String className = clazz.getSimpleName() + "State";
		
		try{
			t = (T) SpringApplicationContext.getBean(className);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return t;
	}
}