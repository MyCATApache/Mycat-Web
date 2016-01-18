# mycat-web
Mycat-web 是对mycat-server提供监控服务。功能不局限于对mycat-server使用。他基于jmx对所有JVM监控。通过JDBC连接对Mycat、Mysql
监控。基于snmp协议，监控远程服务器(目前仅限于linux系统)的cpu、内存、网络、磁盘。

支持SQL上线，基于sqlwatch开源项目，具备SQL语法检查和备份数据识别，释放DBA精力。
###<b> 新增 mycat all in one 镜像下载</b> <br/><br/>
mycat all in one 是 mycat-server,mycat-web,mysql，zookeeper 于一身的测试开发环境，是您开发测试必备良器，
您只需要执行如下几个步骤便可开启属于您的 mycat 之旅 :
> <b>导入 OVA </b> 
>* 安装Oracle VM VirtualBox
>* 启动Oracle VM VirtualBox
>* 下载 mycat-all-in-one 镜像文件，[戳这里下载all-in-one镜像](http://pan.baidu.com/s/1qWMkJPM),密码：v63y  
>* File（管理） -> Import Appliances（导入虚拟电脑）<网络模式首选桥接模式>
>* 选择CentOS 7.ova
>* 一路Next

><b> 启动虚拟机 </b>
>* 登录虚拟机 root/123456
>* 启动多实例Mysql

   ```
      mysqld_multi start
   ```

> <b>启动 Mycat</b>
 ```
    cd /opt/mycat/
    ./bin/mycat start
 ```
 > <b>  ZK启动 </b>
    
```
    cd /opt/zookeeper-3.4.6
	bin/zkServer.sh start
	bin/zkCli.sh
```
> <b>体验 Mycat</b> 
   >* 启动Navicat Premium
   >* 连接Mycat，IP:8066 test/test
   >* 连接TESTDB
   >* 测试

```
      select * from t_user;
```
 > <b>  mycat eye启动 </b>
    
```
    cd /opt/mycat-web
	./start.sh 
    访问地址：http://localhost:8082/mycat
```   
<b>请留意 '体验 Mycat'该步骤中的 IP 地址的设定，虚拟机中 IP 地址若与主机地址不匹配会引发连接失败的情况，
此时可以将 虚拟机IP 地址修改静态IP地址来解决，修改位于路径
````
/etc/sysconfig/network-scripts/ifcfg-enp0s3 
````
下面的文件，然后运行命令 
````
service network restart
````
来让刚刚修改过的文件生效即可

mycat web SQL上线配置参考（可选）
````
修改mycat.properties文件的访问sqlwatch参数配置，具体参考sqlwatch.md
````
</b>


