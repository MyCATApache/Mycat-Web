

INSERT INTO T_OC_DATAHOST VALUES('7bb8c8443eb1419db233aff8a261c05c','localhost1','100','500','1','select user()',current_timestamp(),'系统管理员','native','mysql');



INSERT INTO T_OC_DATAHOSTMAP VALUES('80db4ab918d8477ca1ab5e821502a333','dataHost1','hostM1');

INSERT INTO T_OC_DATAHOSTMAP VALUES('d90cba1e2a2a4aeab8594aafe0918767','dataHost1','hostM2');

INSERT INTO T_OC_DATAHOSTMAP VALUES('3a18c2f8cd2a4f338e45a7f9af0fc7e0','localhost1','hostM1');


INSERT INTO T_OC_DATANODE VALUES('5f5c4e8f9ded4afa98c22f34633a41d4','dn1','localhost1','db1',current_timestamp(),'系统管理员');

INSERT INTO T_OC_DATANODE VALUES('8ec3bdd3d4a644b7be077ff7d1734b24','dn2','localhost1','db2',current_timestamp(),'系统管理员');

INSERT INTO T_OC_DATANODE VALUES('6b8d141a1de24341a0e98a7c275300c4','dn3','localhost1','db3',current_timestamp(),'系统管理员');


INSERT INTO T_OC_FUNCTION VALUES('c89597a00d4c4d679fa2e27f637555df','hash-int','org.opencloudb.route.function.PartitionByFileMap',NULL,current_timestamp(),'系统管理员');

INSERT INTO T_OC_FUNCTION VALUES('868f73d7ad3945c49a09622dbdedcf99','rang-long','org.opencloudb.route.function.AutoPartitionByLong',NULL,current_timestamp(),'系统管理员');

INSERT INTO T_OC_FUNCTION VALUES('50e9789e24e34828ae6dc9a54ba1e317','mod-long','org.opencloudb.route.function.PartionByMod',NULL,current_timestamp(),'系统管理员');


INSERT INTO T_OC_FUNCTION_PARAM VALUES('3','count','mod-long',NULL);

INSERT INTO T_OC_FUNCTION_PARAM VALUES('autopartition-long.txt','mapFile','rang-long',NULL);

INSERT INTO T_OC_FUNCTION_PARAM VALUES('partition-hash-int.txt','mapFile','hash-int',NULL);



INSERT INTO T_OC_MYCAT VALUES('fd5b4523904f436fb2bddf3c38261760','dataNodeHeartbeatPeriod','5000','分片节点心跳检测时间(毫秒)',current_timestamp(),'系统管理员');

INSERT INTO T_OC_MYCAT VALUES('8d0e4c3fb8c64e28be6e02584837fd8f','processorExecutor','10','线程池大小',current_timestamp(),'系统管理员');

INSERT INTO T_OC_MYCAT VALUES('67d9f6b56a344d7b92c234cf29840445','processors','1','CPU核心数',current_timestamp(),'系统管理员');

INSERT INTO T_OC_MYCAT VALUES('bbb21a6ff5a04326b661ce7b35708ef2','serverPort','8066','服务端口',current_timestamp(),'系统管理员');

INSERT INTO T_OC_MYCAT VALUES('c8457d9d25714bb3b95541dfb45e4e83','managerPort','9066','管理端口',current_timestamp(),'系统管理员');



INSERT INTO T_OC_SCHEMA VALUES('2194454b38704d4b900da0f8dbb5a139','TESTDB',current_timestamp(),'系统管理员');

INSERT INTO T_OC_SCHEMA_MAP VALUES('edb00cf516394347995b88963973d8b5','TESTDB','company');

INSERT INTO T_OC_SCHEMA_MAP VALUES('b353930b55014c388d0afbf5f6c74153','TESTDB','goods');

INSERT INTO T_OC_SCHEMA_MAP VALUES('562a22f1d30b48bd9854af681227f82a','TESTDB','customer');

INSERT INTO T_OC_SCHEMA_MAP VALUES('a5b27841c73945a49020c2fe5460f382','TESTDB','hotnews');

INSERT INTO T_OC_SCHEMA_MAP VALUES('ecedfba87ea043cc84152699957fa7eb','TESTDB','travelrecord');

INSERT INTO T_OC_TABLE VALUES('2025cd39cde244a6b064e87733a019a7','company','global','dn2,dn1',NULL,NULL,'','',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLE VALUES('c983d442ebbf4ecd916e3d37d2ac6d72','goods','global','dn1,dn2',NULL,NULL,'','',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLE VALUES('1cddf437da0540759bb389419a8651e6','customer','default','dn1,dn2','sharding-by-intfile',NULL,'','',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLE VALUES('e81c3146a3f9488fb3e51e8647ff6581','orders','default',NULL,'','1cddf437da0540759bb389419a8651e6','customer_id','id',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLE VALUES('52c3958486e74bc297964f55932ec83a','order_items','default',NULL,'','e81c3146a3f9488fb3e51e8647ff6581','order_id','id',current_timestamp(),'系统管理员','open');

INSERT INTO T_OC_TABLE VALUES('9587567cdee54459854758d91377491c','customer_addr','default',NULL,'','e81c3146a3f9488fb3e51e8647ff6581','customer_id','id',current_timestamp(),'系统管理员','open');

INSERT INTO T_OC_TABLE VALUES('a3541bd28a5c46cc98143c4a3ae62e59','hotnews','global','dn1,dn2,dn3',NULL,NULL,'','',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLE VALUES('180d91fdb8dc4c2593bf301143b75aab','travelrecord','global','dn1,dn2,dn3',NULL,NULL,'','',current_timestamp(),'系统管理员','closed');

INSERT INTO T_OC_TABLERULE VALUES('10f70b80079f44e2913740b775d425fd','sharding-by-intfile','sharding_id','hash-int',current_timestamp(),'系统管理员');

INSERT INTO T_OC_TABLERULE VALUES('32777975858e44a69fa51a82861b7313','auto-sharding-long','id','rang-long',current_timestamp(),'系统管理员');

INSERT INTO T_OC_TABLERULE VALUES('155b10c3e588431a81575cca1dd1bc23','mod-long','id','mod-long',current_timestamp(),'系统管理员');



INSERT INTO T_OC_USER VALUES('e7e65d043ac343c0a50c8ae1a7747eff','root','root','','','系统管理员',current_timestamp(),'test');


INSERT INTO T_SYS_CODE VALUES('e55d0b38e07f46218cbc29e7b42292b5','closed','根节点','IS_LEAF','','菜单节点类型',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('e33934f5cbb94a8d80ca522e95bb1c7f','open','叶子节点','IS_LEAF','','菜单节点类型',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('c93a594acd7142c4839b2f994c2defe0','closed','主服务','IS_MASTER','','主-从物理机',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('9b45f5c8260a4e8c90f0fe8a50d8681e','open','从服务','IS_MASTER','','主-从物理机',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('335016ae45bd404b8a617effb577bbf1','default','普通表','OC_TABLE_TYPLE','','oc表类型',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('a56dbad54df34bcf9f3d6320df7360e4','global','全局表','OC_TABLE_TYPLE','','oc表类型',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('87370685533943389b1b220683307534','closed','是','IS_MASTER_TABLE','','主-子表',current_timestamp(),NULL);

INSERT INTO T_SYS_CODE VALUES('e218e99d676f40a99cd0aa7df23a5a95','open','否','IS_MASTER_TABLE','','主-子表',current_timestamp(),NULL);

INSERT INTO T_SYS_MENU VALUES('4ef90b0879aa4475bc61164ed88ca933','SERVICE','RESOURCE','SERVICE',NULL,NULL,'钟良',NULL,3,'服务管理','open');

INSERT INTO T_SYS_MENU VALUES('8d69f70d59374a2b8b162d083e45781b','WHEREEX','RESOURCE','WHEREEX',NULL,NULL,'朱骏',NULL,5,'查询条件管理','open');

INSERT INTO T_SYS_MENU VALUES('0745887ee7bd4bb0a99937bde201f27d','AUTHBUTTON','SYS_POWER','AUTHBUTTON',NULL,NULL,'钟良',NULL,2,'按钮授权','open');

INSERT INTO T_SYS_MENU VALUES('8e8ed316d332490f9f00cabc246fa116','AUTHSERVICE','SYS_POWER','AUTHSERVICE',NULL,NULL,'钟良',NULL,3,'服务授权','open');

INSERT INTO T_SYS_MENU VALUES('08cf5e3ac8de48918daf47344c2b3db3','MYBATIS','RESOURCE','MYBATIS',NULL,NULL,'钟良',NULL,4,'mybatis配置管理','open');

INSERT INTO T_SYS_MENU VALUES('1a364972-fbba-4165-8336-089c1a520c98','BUTTON','RESOURCE','BUTTON',NULL,NULL,'admin',NULL,2,'按钮管理','open');

INSERT INTO T_SYS_MENU VALUES('219ccc49-9ce6-423c-b8e6-c1ac568a239e','RSA','POWER','RSA',NULL,NULL,NULL,NULL,3,'密钥管理','open');

INSERT INTO T_SYS_MENU VALUES('35bef9fa-4fb9-4c5c-ad2e-2d372e3c14b7','ROLE_TYPE','SYSTEM','ROLE_TYPE',NULL,NULL,'admin',NULL,2,'角色类型管理','open');

INSERT INTO T_SYS_MENU VALUES('4917e899-cfe3-49bc-990f-2f0134b13068','AUTHUSER','SYS_POWER','AUTHUSER',NULL,NULL,'admin',NULL,0,'用户授权','open');

INSERT INTO T_SYS_MENU VALUES('4fa18327-2aeb-4481-bf16-01af62bb58e8','CODE','SYSTEM','CODE',NULL,NULL,'admin',NULL,6,'代码管理','open');

INSERT INTO T_SYS_MENU VALUES('54539f08-3ea3-41cb-a116-618702b6432a','USER','SYSTEM','USER',NULL,NULL,'admin',NULL,0,'用户管理','open');

INSERT INTO T_SYS_MENU VALUES('55b9846c-b0ed-46d3-8451-949a43f75cc4','PRODUCT','POWER','PRODUCT',NULL,NULL,'admin',NULL,1,'产品管理','open');

INSERT INTO T_SYS_MENU VALUES('5a9c41b4-3a4b-45ce-a904-024c5053796b','PAGE','RESOURCE','PAGE',NULL,NULL,'admin',NULL,1,'页面管理','open');

INSERT INTO T_SYS_MENU VALUES('6919cce1-e46f-4649-922f-944fec30c5f7','SYS_POWER','SYSTEM',NULL,NULL,NULL,'admin',NULL,4,'系统授权管理','closed');

INSERT INTO T_SYS_MENU VALUES('6b364cb9-3bd9-4048-88a7-476090bed535','RESOURCE','SYSTEM',NULL,NULL,NULL,'admin',NULL,5,'资源管理','closed');

INSERT INTO T_SYS_MENU VALUES('b2c0773e-bd64-4d3d-90b5-053e0076f0fe','ROLE','SYSTEM','ROLE',NULL,NULL,'admin',NULL,3,'角色管理','open');

INSERT INTO T_SYS_MENU VALUES('c51afb8c-4d2c-4e7e-ae7d-a3b78bd71938','LICENSES','POWER','LICENSES',NULL,NULL,NULL,NULL,2,'证书管理','open');

INSERT INTO T_SYS_MENU VALUES('c619e6d6-490a-4af7-8362-f4fa37254d90','SYSTEM',NULL,NULL,NULL,NULL,'admin1',NULL,0,'系统元数据管理','closed');

INSERT INTO T_SYS_MENU VALUES('dbe62fce-8aee-46e4-b58d-78f8f05710a0','MENU','RESOURCE','MENU',NULL,NULL,'admin',NULL,0,'菜单维护','open');

INSERT INTO T_SYS_MENU VALUES('e48b9093-5c60-4cbc-87b2-4223fa3bacc7','AUTHRESOUCE','SYS_POWER','AUTHRESOUCE',NULL,NULL,'admin',NULL,1,'资源授权','open');

INSERT INTO T_SYS_MENU VALUES('e5677ce2-0188-4acb-9352-205b8b35c088','ORG','SYSTEM','ORG',NULL,NULL,'admin',NULL,1,'机构管理','open');

INSERT INTO T_SYS_MENU VALUES('fc22391f-64f3-4cb0-9a4e-de1a5afb162b','CUSTOMER','POWER','CUSTOMER',NULL,NULL,'admin',NULL,0,'客户管理','open');

INSERT INTO T_SYS_MENU VALUES('e80084ef009743418e5156931dbb4fdc','SETUP','SYSTEM','SETUP',NULL,NULL,'钟良',NULL,8,'系统参数设置','open');

INSERT INTO T_SYS_MENU VALUES('261e73f508db466dafa658b6879b8cd9','OPENCLOUD','','',NULL,current_timestamp(),'系统管理员',NULL,1,'MyCat基本配置','closed');

INSERT INTO T_SYS_MENU VALUES('2533d5cee6904852b29986e32c4f94ea','HOST','SCHMEAL_M','HOST',NULL,current_timestamp(),'系统管理员',NULL,0,'物理机管理','open');

INSERT INTO T_SYS_MENU VALUES('17497a2a4b224ce082dd745b2e146498','DATAHOST','SCHMEAL_M','DATAHOST',NULL,current_timestamp(),'系统管理员',NULL,1,'物理节点管理','open');

INSERT INTO T_SYS_MENU VALUES('6f8cdb0f027247f198c4a7e48c83205d','DATANODE','SCHMEAL_M','DATANODE',NULL,current_timestamp(),'系统管理员',NULL,3,'分片节点管理','open');

INSERT INTO T_SYS_MENU VALUES('75a9a58dcc95452180a1d7ae79058ec1','OC_TABLE','SCHMEAL_M','OC_TABLE',NULL,current_timestamp(),'系统管理员',NULL,6,'表管理','open');

INSERT INTO T_SYS_MENU VALUES('e749a99338594699a52822c56de8f39a','DATAHOSTMAP','SCHMEAL_M','DATAHOSTMAP',NULL,current_timestamp(),'系统管理员',NULL,2,'物理节点绑定物理机','open');

INSERT INTO T_SYS_MENU VALUES('609e8ba30d4b4bdb94e2198ac4ee59ff','OC_SCHEMA','SCHMEAL_M','OC_SCHEMA',NULL,current_timestamp(),'系统管理员',NULL,8,'逻辑库管理','open');

INSERT INTO T_SYS_MENU VALUES('bee8f00edd534e9dbfc23aaacec5d89a','OC_TABLERULE','TABLERULE_M','OC_TABLERULE',NULL,current_timestamp(),'系统管理员',NULL,5,'分片规则管理','open');

INSERT INTO T_SYS_MENU VALUES('270222e94bd842fe92bd8b79b73c4bc5','OC_FUNCTION','TABLERULE_M','OC_FUNCTION',NULL,current_timestamp(),'系统管理员',NULL,4,'分片函数管理','open');

INSERT INTO T_SYS_MENU VALUES('b102b6ebff7d4fb6ab74ddf977b0e99c','OC_USER','MYCAT_M','OC_USER',NULL,current_timestamp(),'系统管理员',NULL,9,'MyCat用户管理','open');

INSERT INTO T_SYS_MENU VALUES('68e858cee45f42798baf3669c04355c6','OC_SYSTEM','MYCAT_M','OC_SYSTEM',NULL,current_timestamp(),'系统管理员',NULL,10,'MyCat系统参数配置','open');

INSERT INTO T_SYS_MENU VALUES('b6e92c1ffd694264899e21fcf99b9e04','OC_SCHEMAMAP','SCHMEAL_M','OC_SCHEMAMAP',NULL,current_timestamp(),'系统管理员',NULL,7,'逻辑库添加表','open');

INSERT INTO T_SYS_MENU VALUES('d91ef1033837427082ded0257d04b2a2','SCHMEAL_M','OPENCLOUD','',NULL,current_timestamp(),'系统管理员',NULL,2,'逻辑库管理','closed');

INSERT INTO T_SYS_MENU VALUES('9a27a4ff7da9464198a6cb3125672acc','TABLERULE_M','OPENCLOUD','',NULL,current_timestamp(),'系统管理员',NULL,1,'分片规则管理','closed');

INSERT INTO T_SYS_MENU VALUES('56bb8d4a33e04008918f45b1314eb228','MYCAT_M','OPENCLOUD','',NULL,current_timestamp(),'系统管理员',NULL,0,'MyCat服务管理','closed');

INSERT INTO T_SYS_MENU VALUES('da890a77998846d3855647575300473d','MONITOR','','',NULL,current_timestamp(),'系统管理员',NULL,2,'Mycat性能监控','closed');

INSERT INTO T_SYS_MENU VALUES('7d8aacc3e894427f8b39de56749ca1db','BACKEND','MONITOR','BACKEND',NULL,current_timestamp(),'系统管理员',NULL,0,'物理连接信息','open');

INSERT INTO T_SYS_MENU VALUES('738de4c260c04592943766b51df58b41','CONNECTION','MONITOR','CONNECTION',NULL,current_timestamp(),'系统管理员',NULL,1,'客户端连接信息','open');

INSERT INTO T_SYS_MENU VALUES('056319de967d431f98a79f3355b5b678','THREADPOOL','MONITOR','THREADPOOL',NULL,current_timestamp(),'系统管理员',NULL,2,'线程池信息','open');

INSERT INTO T_SYS_MENU VALUES('15890e52fe924ee1aedf7f6c82add965','HEARTBEAT','MONITOR','HEARTBEAT',NULL,current_timestamp(),'系统管理员',NULL,3,'物理库心跳检测','open');

INSERT INTO T_SYS_MENU VALUES('c3f2ed3090c8441591a603523b91d178','MO_DATANODE','MONITOR','MO_DATANODE',NULL,current_timestamp(),'系统管理员',NULL,4,'数据节点的访问情况','open');

INSERT INTO T_SYS_MENU VALUES('af27164b5d744dbdae5197de968494c7','DATASOURCE','MONITOR','DATASOURCE',NULL,current_timestamp(),'系统管理员',NULL,5,'数据源信息','open');


INSERT INTO T_SYS_PAGE VALUES('db62945680c04f4da32658a203cf0277','SERVICE','/page/system/jsp/service.jsp','服务管理',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('25a8cee28e4042f78a3d9c451ad2834d','AUTHBUTTON','/page/system/jsp/authbutton.jsp','按钮授权',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('75ae063dcdad49679e262cf49e3446f4','WHEREEX','/page/system/jsp/whereex.jsp','查询条件管理',NULL,'朱骏','1');

INSERT INTO T_SYS_PAGE VALUES('dfe96a879d374d839859605ad07c1144','AUTHSERVICE','/page/system/jsp/authservice.jsp','服务授权',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('84b7062f63f842cd8ed14d9616f0314a','OWNER','/page/system/jsp/ownermapdb.jsp','数据源分库管理',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('73389995efd14c69b5f0494bcf5350ed','MYBATIS','/page/system/jsp/mybatis.jsp','mybatis配置管理',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('81fad78058324e49919e94a6d0da4876','SUGGESTION','/page/system/jsp/suggestion.jsp','用户意见反馈',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('ca792324d8da4de09da167b3e49cd724','CUSTOMER','/page/sa/jsp/customer.jsp','客户管理',NULL,NULL,'1');

INSERT INTO T_SYS_PAGE VALUES('14b53ac7-55f1-48f1-8203-7fe1ef8d85b7','MENU','/page/system/jsp/menu.jsp','菜单维护',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('2d7cd4ac-20f4-4278-853d-9901127df1fe','AUTHUSER','/page/system/jsp/authuser.jsp','用户授权',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('37fd09dd-66c4-440e-a268-fa15768579e6','PAGE','/page/system/jsp/page.jsp','页面管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('68bfd79b-9ce8-4d67-9e09-ff856c096ff5','ORG','/page/system/jsp/org.jsp','机构管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('918eb82e-90a0-4a45-814e-0614f89d3ea3','CODE','/page/system/jsp/code.jsp','代码管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('9226052e-37df-4c0f-81e4-2c1b17672794','USER','/page/system/jsp/user.jsp','用户管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('a918c935-dbc5-448b-8e4c-375d0c636f5f','BUTTON','/page/system/jsp/button.jsp','按钮管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('aba9840e-83cf-4b40-8d8f-35bcf73dcf81','AUTHRESOUCE','/page/system/jsp/authresouce.jsp','资源授权',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('bd6a7e1c-5d5d-456a-817d-c2beec456324','ROLE','/page/system/jsp/role.jsp','角色管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('bed0b9a7-2834-4034-a693-b8390e14415a','ROLE_TYPE','/page/system/jsp/roletype.jsp','角色类型管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('c005eef2-b2f2-4f3c-93f0-56a52e61364c','LICENSES','/page/sa/jsp/licenses.jsp','证书管理',NULL,NULL,'1');

INSERT INTO T_SYS_PAGE VALUES('e2ed2eba-08d6-4c5f-8b4d-844e2b46fc8d','PRODUCT','/page/sa/jsp/product.jsp','产品管理',NULL,NULL,'1');

INSERT INTO T_SYS_PAGE VALUES('f68fed18-0b90-4054-80ff-7d427effb4ce','RSA','/page/sa/jsp/rsa.jsp','密钥管理',NULL,'admin','1');

INSERT INTO T_SYS_PAGE VALUES('e72bc2a3e9b34bfbaa544017981d05f4','SETUP','/page/system/jsp/setup.jsp','系统参数设置',NULL,'钟良','1');

INSERT INTO T_SYS_PAGE VALUES('7753a138d77b40b396d61155a15c82a4','DATAHOST','/page/oc/manager/jsp/datahost.jsp','物理节点管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('802f0c37203944348acd176b05556b1a','HOST','/page/oc/manager/jsp/host.jsp','物理机管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('fa4eda40ade240669351b331a9b09de2','DATANODE','/page/oc/manager/jsp/datanode.jsp','分片节点管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('f039c10860f144478fdd694dcc1dfcfe','OC_TABLE','/page/oc/manager/jsp/table.jsp','表管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('8c95a47d75eb4fbbb89e7905f7571d93','DATAHOSTMAP','/page/oc/manager/jsp/datahostmap.jsp','物理节点绑定物理机',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('be3fb4fd996649d1b5124e536b3d68c3','OC_TABLERULE','/page/oc/manager/jsp/tablerule.jsp','分片规则管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('bd2acc8ba3864638aed4642e181d5f08','OC_FUNCTION','/page/oc/manager/jsp/function.jsp','分片函数管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('844eb3352a0644cc9f3a6ec6455fee00','OC_SCHEMA','/page/oc/manager/jsp/schema.jsp','逻辑库管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('d4751070f5514d02ae626e36ffdb708f','OC_USER','/page/oc/manager/jsp/user.jsp','MyCat用户管理',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('7fd086c90cd84d759e45010aea47eae1','OC_SYSTEM','/page/oc/manager/jsp/mycat.jsp','MyCat系统参数配置',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('b271474df1e1455199133a548c615e83','OC_SCHEMAMAP','/page/oc/manager/jsp/schemaMap.jsp','逻辑库添加表',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('21d2e2268b4a4fc88ed24348b165de93','BACKEND','/page/oc/monitor/jsp/backend.jsp','物理连接信息',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('ce07a19cc1bd4cc1a85d6bc25527d396','CONNECTION','/page/oc/monitor/jsp/connection.jsp','客户端连接信息',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('223d805152b34c688f4370aeeffeb41e','THREADPOOL','/page/oc/monitor/jsp/threadpool.jsp','线程池信息',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('cb3cb3181c2747188a28dc14c8b57e4c','HEARTBEAT','/page/oc/monitor/jsp/heartbeat.jsp','物理库心跳检测',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('7aad1e774f274cdfa5ad785696a7fe0f','MO_DATANODE','/page/oc/monitor/jsp/datanode.jsp','数据节点的访问情况',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_PAGE VALUES('fb7dbccbe9454b61a05660da3b7cef31','DATASOURCE','/page/oc/monitor/jsp/datasource.jsp','数据源信息',current_timestamp(),'系统管理员','1');

INSERT INTO T_SYS_SETUP VALUES('538ad0fb66e649f0b2f241adb26e7d5c','显示页面路径','showMenu','在网站右下角显示菜单对应的页面的路径','test','zhongliang',NULL,'1');


INSERT INTO T_SYS_USER VALUES('49c4f8a6-d192-4557-b650-1f1f8795cf46','admin','系统管理员','21232f297a57a5a743894a0e4a801fc3','0',NULL,'admin','13764178983',NULL,100,0,1,'jiefangbu',NULL,0,'accp_huangxin@163.com','0','0');
