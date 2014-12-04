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

import java.io.File;

public class CreatIntall {
	public static void main(String[] args) {
		CreatIntall.createMaven();
	}
	
	public static void createMaven(){
		File file = new File("E:\\hx\\mywork\\rainbow\\rainbow\\rainbow-common\\lib\\new");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File filechild : files) {
				String fileName = filechild.getName();
//				System.out.println(fileName);
				String artifactId = fileName.substring(0,fileName.lastIndexOf('-'));
				String version = fileName.substring(fileName.lastIndexOf('-')+1,fileName.lastIndexOf('.'));
//				System.out.println("mvn install:install-file -DgroupId=org.rainbow.lib -DartifactId="+artifactId+" -Dversion="+version+" -Dclassifier=deps  -Dpackaging=jar -Dfile="+fileName+"");
//				System.out.println("mvn install:install-file -DgroupId=org.rainbow.lib -DartifactId="+artifactId+" -Dversion="+version+"  -Dpackaging=jar -Dfile="+fileName+"");
				
					System.out.println("<dependency>");
					System.out.println("    <groupId>org.rainbow.lib</groupId>");
					System.out.println("    <artifactId>"+artifactId+"</artifactId>");
					System.out.println("    <version>${"+artifactId+".version}</version>");
					System.out.println("</dependency>");
					System.out.println("<"+artifactId+".version>"+version+"</"+artifactId+".version>");
			}
		}
	
	}
	
	
	public static void changeSpringJarName(){
		File file = new File("E:\\hx\\mywork\\lib\\spring-framework-3.1.3.RELEASE\\dist");
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File filechild : files) {
				String fileName = filechild.getName();
				//String gropid = fileName.substring(0,fileName.lastIndexOf(".", fileName.indexOf('-')));
				String artifactId = fileName.substring(fileName.lastIndexOf('.',fileName.lastIndexOf('-'))+1,fileName.lastIndexOf('-'));
				String version = fileName.substring(fileName.lastIndexOf('-')+1,fileName.lastIndexOf('.'));
				System.out.println(filechild.getParent()+"\\spring-"+artifactId+"-"+version+".jar");
				filechild.renameTo(new File(filechild.getParent()+"\\spring-"+artifactId+"-"+version+".jar"));

			}
		}
	}
}

