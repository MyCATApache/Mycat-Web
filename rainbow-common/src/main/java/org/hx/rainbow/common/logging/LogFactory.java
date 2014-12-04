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
package org.hx.rainbow.common.logging;

import java.lang.reflect.Constructor;

import org.apache.ibatis.io.Resources;


public final class LogFactory {
	public static final String GLOBAL_LOGGER_NAME = "global";
	private static Constructor<? extends Log> logConstructor;

	public static Log getLog(Class<?> aClass) {
		return getLog(aClass.getName());
	}

	public static Log getLog(String logger) {
		try {
			return (Log) logConstructor.newInstance(new Object[] { logger });
		} catch (Throwable t) {
			throw new LogException("Error creating logger for logger " + logger
					+ ".  Cause: " + t, t);
		}
	}

	public static synchronized void useSlf4jLogging() {
		setImplementation("org.hx.rainbow.common.logging.slf4j.Slf4jImpl");
	}

	public static synchronized void useCommonsLogging() {
		setImplementation("org.hx.rainbow.common.logging.commons.JakartaCommonsLoggingImpl");
	}

	public static synchronized void useLog4JLogging() {
		setImplementation("org.hx.rainbow.common.logging.log4j.Log4jImpl");
	}

	public static synchronized void useJdkLogging() {
		setImplementation("org.hx.rainbow.common.logging.jdk14.Jdk14LoggingImpl");
	}

	public static synchronized void useStdOutLogging() {
		setImplementation("org.hx.rainbow.common.logging.stdout.StdOutImpl");
	}

	public static synchronized void useNoLogging() {
		setImplementation("org.hx.rainbow.common.logging.nologging.NoLoggingImpl");
	}

	private static void tryImplementation(Runnable runnable) {
		if (logConstructor == null)
			try {
				runnable.run();
			} catch (Throwable t) {
			}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setImplementation(String implClassName) {
		try {
			Class<?> implClass = Resources.classForName(implClassName);
			Constructor candidate = implClass.getConstructor(new Class[] { String.class });
			Log log = (Log) candidate
					.newInstance(new Object[] { LogFactory.class.getName() });
			log.debug("Logging initialized using '" + implClassName
					+ "' adapter.");
			logConstructor = candidate;
		} catch (Throwable t) {
			throw new LogException("Error setting Log implementation.  Cause: "
					+ t, t);
		}
	}

	static {
		tryImplementation(new Runnable() {
			public void run() {
				LogFactory.useSlf4jLogging();
			}
		});
		tryImplementation(new Runnable() {
			public void run() {
				LogFactory.useCommonsLogging();
			}
		});
		tryImplementation(new Runnable() {
			public void run() {
				LogFactory.useLog4JLogging();
			}
		});
		tryImplementation(new Runnable() {
			public void run() {
				LogFactory.useJdkLogging();
			}
		});
		tryImplementation(new Runnable() {
			public void run() {
				LogFactory.useNoLogging();
			}
		});
	}
}