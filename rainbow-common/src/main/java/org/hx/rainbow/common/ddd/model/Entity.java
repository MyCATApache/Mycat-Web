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
package org.hx.rainbow.common.ddd.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hx.rainbow.common.ddd.annotation.Lazyload;


/**
 * An entity, as explained in the DDD book.
 * 
 */
public abstract class Entity<V extends IEntityState> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String _objectId = null;
	protected EntityState _state = EntityState.READ;
	
	/**
	 * 子对象锁定集合类
	 */
	private Set<V> _rstate = new HashSet<V>();
	 
	protected Set<V> _bstate = new LinkedHashSet<V>();


	/**
	 * Entities compare by identity, not by attributes.
	 * 
	 * @param other
	 *            The other entity.
	 * @return true if the identities are the same, regardles of other
	 *         attributes.
	 */
	@Lazyload(enmuClass = GeneralState.class, state = "SELF")
	public  void reload(){
		
	}
	
	public Set<V> getBstate(){
		return _bstate;
	} 

	public void setBstate(V bstate) {
		this._bstate.add(bstate);
	}
	

	public boolean sameIdentityAs(Entity<V> other) {
		return other._objectId.equals(this._objectId);
	}

	public String getObjectId() {
		return _objectId;
	}

	public void setObjectId(String objectId) {
		this._objectId = objectId;
	}

	public EntityState getState() {
		return _state;
	}

	public void setState(EntityState state) {
		this._state = state;
	}
	
	/**
	 * 锁定子对象,子对象mGetXX() 不在执行数据库加载
	 * @param state
	 */
	public void markRstate(V state){
		this._rstate.add(state);
	}

	/**
	 * 释放子对象，mGetXX()方法时，执行数据库加载
	 * @param state
	 */
	public void resetRstate(V state){
		this._rstate.remove(state);
	}
	/**
	 * 判断子对象是否被锁定
	 * @param state
	 * @return
	 */
	public boolean isNeedRead(V state){
		return this._rstate.contains(state);
	}

}