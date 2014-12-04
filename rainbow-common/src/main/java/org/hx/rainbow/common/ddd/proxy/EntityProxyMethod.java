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
package org.hx.rainbow.common.ddd.proxy;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.annotation.Lazyload;
import org.hx.rainbow.common.ddd.base.impl.RepositoryBase;
import org.hx.rainbow.common.ddd.factory.RepositoryFactory;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.CglibUitl;



/**
 * 
 * @author huangxin
 *
 * @param <T>
 */
public class EntityProxyMethod implements MethodInterceptor {
	

	private volatile static EntityProxyMethod entityProxyMethod =  null;
	
	public static EntityProxyMethod getInstance(){
		if (entityProxyMethod == null) {
			synchronized (EntityProxyMethod.class) {
				if (entityProxyMethod == null) {
					entityProxyMethod = new EntityProxyMethod();
				}
			}
		}
		return entityProxyMethod;
	}


	/**
	 * 创建代理对象
	 * 
	 * @param target
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public Object getInstance(Class clazz) {	
	Enhancer enhancer = new Enhancer();
	enhancer.setSuperclass(clazz);
	enhancer.setCallback(this);
	return enhancer.create();
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {

			if(!method.isAnnotationPresent(Lazyload.class)){
				return proxy.invokeSuper(obj, args);
			}
			Lazyload lazyload	= method.getAnnotation(Lazyload.class);
			if(lazyload == null){
				return proxy.invokeSuper(obj, args);
			}
			IEntityState iEntityState = (IEntityState)Enum.valueOf((Class)lazyload.enmuClass(), lazyload.state());
			if(iEntityState == null){
				return proxy.invokeSuper(obj, args);
			}
			if(obj instanceof Entity){
				retrieve((Entity)obj,args,iEntityState);
			}
			return proxy.invokeSuper(obj, args);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void retrieve(Entity<IEntityState> obj,Object[] args,IEntityState iEntityState) throws Exception{

		Map<String,Object> map = null;
		if(args.length >0){
			if(args[0] instanceof Map){
				map = (Map<String,Object>)args[0];
			}
		}
		Entity superObject = obj;
		Class clazz = obj.getClass();
		if(CglibUitl.getInstance().isCglib(clazz)){
			clazz = clazz.getSuperclass();
		}
		if(clazz.isAnnotationPresent(Aggergation.class)){
			Aggergation aggergation = (Aggergation) clazz.getAnnotation(Aggergation.class);
			Class<?> root = aggergation.root();
			boolean isSubclass = aggergation.isSubclass();
			if(!isSubclass && root != null){
				String superMethodName = "get" + root.getSimpleName();
				Method superMethod = obj.getClass().getMethod(superMethodName,new Class<?>[] {});
				superObject = (Entity)superMethod.invoke(obj, new Object[]{});
				if(superObject == null){
					throw new AppException(clazz.getName()+"."+superMethodName +"();返回值为:null!");
				}
			}
		}
		if(!superObject.isNeedRead(iEntityState)){
			RepositoryBase repositoryBase = RepositoryFactory.getInstance().getRepository(clazz);
			repositoryBase.retrieve(obj,superObject,map,iEntityState);
			if(!"SELF".equals(iEntityState.toString())){
				superObject.markRstate(iEntityState);
			}
		}
	}
	
}

