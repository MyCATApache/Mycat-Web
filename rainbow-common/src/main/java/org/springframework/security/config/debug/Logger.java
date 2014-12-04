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
package org.springframework.security.config.debug;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class Logger
{
  static final Log logger = LogFactory.getLog("Spring Security Debugger");

  void log(String message) {
    log(message, false);
  }

  void log(String message, boolean dumpStack) {
//    StringBuilder output = new StringBuilder(256);
//    output.append("\n\n************************************************************\n\n");
//    output.append(message).append("\n");
//
//    if (dumpStack) {
//      StringWriter os = new StringWriter();
//      new Exception().printStackTrace(new PrintWriter(os));
//      StringBuffer buffer = os.getBuffer();
//
//      int start = buffer.indexOf("java.lang.Exception");
//      buffer.replace(start, start + 19, "");
//      output.append("\nCall stack: \n").append(os.toString());
//    }
//
//    output.append("\n\n************************************************************\n\n");

//    logger.info(output.toString());
  }
}