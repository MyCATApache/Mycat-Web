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
package org.rainbow.common;


public class Test {
	public static void main(String[] args) {
//		long begin = System.currentTimeMillis();
//		int aa = 0;
//		for(int i = 0; i < 100; i++){
//			//System.out.println(i & 4*2-1);
//			//System.out.println( Long.valueOf(i % 8).intValue());//值转换);
//			Integer hashCode = new HashCodeBuilder().append(new ObjectId().toString().toCharArray()).toHashCode();
//			System.out.println((hashCode & 0x7FFFFFFF) % 7);
////			aa = hashCode & 7;
////			aa = (hashCode & 0x7FFFFFFF) % 7;
//		}
//		System.out.println(System.currentTimeMillis() - begin);
		String aa = "0,6,6,2,5,6,3,1,3,3,1,4,1,1,6,5,6,2,6,4,3,0,2,2,1,2,6,5,3,1,1,5,0,4,4,0,2,6,1,2,5,1,3,4,2,2,4,0,0,1,3,0,0,2,5,3,4,5,1,0,2,3,5,0,1,3,3,5,3,0,1,3,0,1,4,2,0,6,3,2,5,1,2,6,1,1,1,0,3,0,3,2,0,1,0,1,5,3,2,3";
//		String bb = "3,2,2,2,1,2,2,5,0,1,0,3,4,6,1,0,4,5,6,1,5,6,2,6,0,1,2,7,0,6,0,6,5,3,2,3,6,0,3,6,7,7,0,6,6,5,0,3,2,0,7,3,1,6,7,1,3,7,7,7,0,2,3,1,3,2,0,4,3,7,5,1,7,1,4,3,7,0,7,0,6,6,7,6,1,6,1,7,1,6,5,5,1,5,1,2,1,1,4,3";
		for(int j = 0;j < 7;j++){
			int ii = 0;
			for(String i : aa.split(",")){
				if(i.endsWith(String.valueOf(j))){
					ii++;
				}
			}
			System.out.println(j+"数量为:" + ii);
		}
		
//		0数量为:17
//		1数量为:20
//		2数量为:16
//		3数量为:18
//		4数量为:8
//		5数量为:11
//		6数量为:10
//		7数量为:0
		
//		0数量为:14
//		1数量为:17
//		2数量为:12
//		3数量为:13
//		4数量为:5
//		5数量为:9
//		6数量为:16
//		7数量为:14
//		Integer hashCode = new HashCodeBuilder().append("aaa".toCharArray()).toHashCode();
//		System.out.println(hashCode);
//		System.out.println("aaa".hashCode());
	}
}