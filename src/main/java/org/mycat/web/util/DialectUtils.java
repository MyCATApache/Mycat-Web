package org.mycat.web.util;

public class DialectUtils {

	public static final String DEFAULT_MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

	public static String getMySQLURL(String ip, String port, String server) {
		return "jdbc:mysql://" + ip + ":" + port + "/" + server + "?characterEncoding=utf8";
	}
}
