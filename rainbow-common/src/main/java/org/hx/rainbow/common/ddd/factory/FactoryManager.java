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

import org.apache.log4j.Logger;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.base.impl.FactoryBase;
import org.hx.rainbow.common.util.CglibUitl;

/**
 * 工厂管理
 * @author huangxin
 *
 */
public class FactoryManager {
	private static final Logger logger = Logger.getLogger(RepositoryFactory.class);
	private volatile static FactoryManager factoryManager = null;
	private FactoryManager(){
		
	}

	public static FactoryManager getInstance(){
		if(factoryManager == null){
			synchronized (FactoryManager.class) {
				if(factoryManager == null){
					factoryManager = new FactoryManager();
				}
			}
		}
		return factoryManager;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends FactoryBase> T getFactory(Class clazz){
		if(clazz == null) return null;
		T t = null;
		try {
			if(CglibUitl.getInstance().isCglib(clazz)){
				clazz = clazz.getSuperclass();
			}
			if(clazz.isAnnotationPresent(Aggergation.class)){
				Aggergation aggergation = (Aggergation) clazz.getAnnotation(Aggergation.class);
				Class<?> root = aggergation.root();
				if(root != null){
					clazz = root;
				}
			}
			String beanName = clazz.getSimpleName() + "Factory";
			
			t = (T) SpringApplicationContext.getBean(beanName);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			ex.printStackTrace();
		}
		return t;
	}
}