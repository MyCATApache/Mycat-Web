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

    /** * zk的命名空间 */
    public static final String LOCAL_ZK_NS_NAME = "";
    public static final String LOCAL_ZK_URL_NAME = "mycat";

    /*****************zookeeper配置节点路径*******************/
    
    /** 特定中心  */
    public static final String MYCAT_ZONES = "/zones";
    public static final String MYCAT_ZONE= MYCAT_ZONES + "/zone-";
    public static final String MYCAT_NODES = "/clusters";
    public static final String MYCAT_NODE = MYCAT_NODES + "/cluster-";
    public static final String MYCAT_SERVERS = "/servers";
    public static final String MYCAT_SERVER = MYCAT_SERVERS + "/server-";
    public static final String MYCAT_HOSTS = "/hosts";
    public static final String MYCAT_HOST = MYCAT_HOSTS + "/host-";
    /** mycat-server负载  */
    public static final String MYCAT_LBS ="/lbs-";
    /** mycat-server负载组  */
    public static final String MYCAT_LBS_GROUP="/lbs_group-";
    /** mysql数据库节点  */
    public static final String MYCAT_MYSQLS="/mysqls-";
    /** mysql数据库组 */
    public static final String MYCAT_MYSQLGROUP="/mysqlgroup-";
    
    
}