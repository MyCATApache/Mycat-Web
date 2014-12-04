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
package org.hx.rainbow.common.security.rsa.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class SecurityUtil {
	private volatile static SecurityUtil  securityUtil = null;
	
	private SecurityUtil(){}
	
	public static SecurityUtil getInstance(){
		if(securityUtil == null){
			synchronized (SecurityUtil.class) {
				if(securityUtil == null){
					securityUtil =  new SecurityUtil();
				}
			}
		}
		return securityUtil;
	}
	
	/**
	 * 加密
	 * @param privateKey 私钥
	 * @param srcBytes 明文
	 * @return
	 */
	public  byte[] encrypt(PrivateKey privateKey,byte[] srcBytes){
		if(privateKey != null){
			try{
				Cipher cipher = Cipher.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider());//Cipher.getInstance("RSA/ECB/PKCS1Padding");    
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);   
				return cipher.doFinal(srcBytes);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 解密
	 * @param publicKey 公钥
	 * @param encBytes 密文
	 * @return
	 */
	public  byte[] decrypt(PublicKey publicKey,byte[] encBytes){
		if(publicKey != null){
			try{
				Cipher cipher = Cipher.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider());//Cipher.getInstance("RSA/ECB/PKCS1Padding");   
				cipher.init(Cipher.DECRYPT_MODE, publicKey); 
				return cipher.doFinal(encBytes);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	
    /**
     * 获取公钥
     * @param modulus 密钥modulus值
     * @param privateExponent 公钥的Exponent值
     * @return
     * @throws Exception
     */
    public  PublicKey getPublicKey(String modulus,String publicExponent) throws Exception {   
    	  RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
    	  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    	  PublicKey publicKey = keyFactory.generatePublic(keySpec);
    	  return publicKey;
    }   


    /**
     * 获取私钥
     * @param modulus 密钥modulus值
     * @param privateExponent 私钥的Exponent值
     * @return
     * @throws Exception
     */
	  public  PrivateKey getPrivateKey(String modulus,String privateExponent) throws Exception {   
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;   
	  }  
  

    /**
     * 获取创建密钥的Modulus值
     * @param keyPair 密钥对  
     * @return
     */
    public  String getModulus(KeyPair keyPair){
    	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    	  return publicKey.getModulus().toString(); 
    	  
    }
    
    /**
     * 获取创建公钥的Exponent值
     * @param keyPair 密钥对  
     * @return
     */
    public  String getPublicExponent(KeyPair keyPair){
    	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    	return  publicKey.getPublicExponent().toString(); 
    	  
    }
    
    /**
     * 获取创建私钥的Exponent值
     * @param keyPair 密钥对  
     * @return
     */
    public  String getPrivateExponent(KeyPair keyPair){
    	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    	return  privateKey.getPrivateExponent().toString(); 
    	  
    }
   
    /**
     * 返回一个密钥对 
     * @return
     * @throws Exception
     */
    public  KeyPair getKeyPair() throws Exception {
    	 KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");   
         //密钥位数   
         keyPairGen.initialize(512);   
         //密钥对   
         return keyPairGen.generateKeyPair();
    }
    
	public static void main(String[] args) {
		try{
			KeyPair keyPair =  SecurityUtil.getInstance().getKeyPair();
			String modulus = SecurityUtil.getInstance().getModulus(keyPair);
			String publicexponent = SecurityUtil.getInstance().getPublicExponent(keyPair);
			String privateexponent = SecurityUtil.getInstance().getPrivateExponent(keyPair);
			
			
			System.out.println("modulus="+modulus);
			System.out.println("publicexponent="+publicexponent);
			System.out.println("privateexponent="+privateexponent);
			
			// 公钥   
	      //  PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();   
	
	        // 私钥   
	      //  PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   
//		   PublicKey publicKey = SecurityUtil.getInstance().getPublicKey("6605150571392906852127292664834649778480000715460249750756591708596101250877128405030946855426808188489739277485876062023576918391397169689952556464697817","65537");   
//		   PrivateKey  privateKey = SecurityUtil.getInstance().getPrivateKey("6605150571392906852127292664834649778480000715460249750756591708596101250877128405030946855426808188489739277485876062023576918391397169689952556464697817","6487433619634557901102914407780972146893802982337623269396692733732432693252656384164243771821639928310855430867301314614312771972740353624274714407120885");  
//	
//		    byte[] enBytes =SecurityUtil.getInstance().encrypt(privateKey,"2096765;fwm78".getBytes()); 
//		    Base64Encoder enc=new Base64Encoder();
		   //BASE64Decoder dec=new BASE64Decoder(); 
	
//		    System.out.println("加密==" + enBytes);   
		    
/*		    byte[] deStr =SecurityUtil.getInstance().decrypt(publicKey,dec.decodeBuffer("dIaXNAg5z4ZBqnJs0jt+nSZO4PFoPf5ny/VEOKr52F1oIwrSCGyGpdkDXx8RPWv3l59JqmwdAB1fW7Ik5LJa1Q==")); 
	        System.out.println("解密==" +new String(deStr));  */ 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	
	}
}