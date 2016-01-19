# mycat-web（sqlwatch)

Mycat-web sqlwatch 能够方便sql上线，当前包括SQL语法检查和备份表的识别2个主要功能。

基于mycat-web定制开发，后台SQL解析使用了sqlwatch开源项目，需要编译部署，

<b>优势：</b>

1、相对于常规的SQL验证办法，优势在于DBA无需准备繁琐的数据环境。

2、提供web方式接入，方便研发等自助，释放DBA精力的同时提高SQL准入的质量。


<b>配置：</b>

1、因为后台SQL解析依赖于sqlwatch项目，需要下载sqlwatch源码并编译为mysqld可执行文件，重命名为sqlwatch放入/usr/bin/目录

具体代码请参考https://github.com/zjjxxlgb/sqlwatch。

测试命令如下，/tmp/sqlcheck1.sql为测试SQL样本。

sqlwatch   </tmp/sqlcheck1.sql 2>&1 


2、同时在Mycat-web的mycat.properties文件配置指定sqlonline相关IP，用户名和密码等访问参数。

sqlonline.server=192.168.80.128

sqlonline.user=root

sqlonline.passwd=123456


