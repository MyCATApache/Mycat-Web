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
package org.hx.rainbow.common.security.rsa.impl;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.hx.rainbow.common.security.rsa.RainbowSecurity;
import org.hx.rainbow.common.security.rsa.util.SecurityUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class RainbowSecurityImpl implements RainbowSecurity{
	

	public String decrypt(String securityCode, String modulus, String publicExponent) {
		String deStr = null;
		try{
			if(securityCode != null){
				BASE64Decoder dec=new BASE64Decoder(); 
				PublicKey publicKey = SecurityUtil.getInstance().getPublicKey(modulus,publicExponent);   
				deStr = new String(SecurityUtil.getInstance().decrypt(publicKey,dec.decodeBuffer(securityCode))); 
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return deStr;
	}


	public String encrypt(String securityCode, String modulus, String privateExponent) {
		String encStr = null;
		try{
			if(securityCode != null){
				BASE64Encoder enc=new BASE64Encoder();
				PrivateKey privateKey = SecurityUtil.getInstance().getPrivateKey(modulus,privateExponent);   
				byte[] encByte = SecurityUtil.getInstance().encrypt(privateKey, securityCode.getBytes()); 
				encStr = enc.encode(encByte);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return encStr;
	}
}