package org.mycat.web.util;


/**
 * 常量类
 * 
 */
public final class Constant {
    private Constant() {

    }

    /** * 统一的编码 */
    public static final String CHARSET = "UTF-8";

    public static final String LOCAL_ZK_URL_NAME = "mycat";
    public static final String MYCAT_CLUSTER_KEY = "mycat-cluster";
    public static final String MYCAT_ZONE_KEY = "mycat-zones";
    public static final String MYCAT_NODES_KEY = "mycat-nodes";
    public static final String MYCAT_HOST_KEY = "mycat-hosts";
    public static final String MYCAT_MYSQLS_KEY = "mycat-mysqls";
    public static final String MYCAT_MYSQL_GROUP_KEY = "mycat-mysqlgroup";
    
    public static final String MYCAT_EYE="/mycat-eye";
    
    public static final String CLUSTER_USER = "user";
    public static final String CLUSTER_DATANODE = "datanode";
    public static final String CLUSTER_DATAHOST = "datahost";
    public static final String CLUSTER_SCHEMA = "schema";
    public static final String CLUSTER_RULE = "rule";
    public static final String CLUSTER_SEQUENCE = "sequence";
    public static final String CLUSTER_BLOCKSQLS = "blockSQLs";
    
    
    public  static final String MYCATS = MYCAT_EYE+"/mycat";
    public  static final String MYCAT_JMX = MYCAT_EYE+"/mycat_jmx";
    public  static final String MYCAT_MYSQL = MYCAT_EYE+"/mysql";
    public  static final String MYCAT_SNMP = MYCAT_EYE+"/mycat_snmp";
    public  static final String MYCAT_PROCESSOR = MYCAT_EYE+"/mycat_processor";	
    
    public static boolean Mycat_JRDS=false;
}