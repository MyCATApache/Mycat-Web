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
package org.hx.rainbow.common.ddd.base.impl;

import java.util.Map;
import java.util.Set;

import org.hx.rainbow.common.dao.Dao;
import org.hx.rainbow.common.ddd.factory.RepositoryStateFactory;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;

@SuppressWarnings("rawtypes")
public abstract class RepositoryBase<K extends Entity,V extends Entity>{

	public Dao dao;
	
	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void retrieve(K entity, V aggregation,Map<String,Object> param,IEntityState state) {	
		System.out.println("entity.class==" +entity.getClass().getName()+"aggregation.class==="+aggregation.getClass().getName());
        RepositoryStateFactory.getInstance().getRepositoryState(aggregation,state).retrieve(entity,aggregation,param);
	}

	@SuppressWarnings("unchecked")
	public void store(V entity) {
		 Set<IEntityState> bstateList = entity.getBstate();
		 for(IEntityState state : bstateList){
			 RepositoryStateFactory.getInstance().getRepositoryState(entity,state).store(entity);
		 }
	}
	
	
}