/*
Navicat MySQL Data Transfer

Source Server         : MySQL3306
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : mycat-web

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2016-01-07 11:13:46
*/

SET FOREIGN_KEY_CHECKS=0;
 
DROP TABLE IF EXISTS `t_mycat_syssql`;
CREATE TABLE `t_mycat_syssql` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `START_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `EXECUTE_TIME` int(11) NOT NULL,
  `USER` varchar(25) NOT NULL,
  `CONTENT` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
 