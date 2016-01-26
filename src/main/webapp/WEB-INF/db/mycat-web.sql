/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50534
Source Host           : localhost:3306
Source Database       : mycat-web

Target Server Type    : MYSQL
Target Server Version : 50534
File Encoding         : 65001

Date: 2016-01-19 17:01:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_mycat_syssql`
-- ----------------------------
DROP TABLE IF EXISTS `t_mycat_syssql`;
CREATE TABLE `t_mycat_syssql` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `ID` int(11) NOT NULL DEFAULT '0',
  `DB_NAME` varchar(30) DEFAULT NULL,
  `START_TIME` bigint(15) NOT NULL,
  `EXECUTE_TIME` int(11) NOT NULL,
  `USER` varchar(25) NOT NULL,
  `CONTENT` text NOT NULL,
  `START_TM` datetime NOT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB AUTO_INCREMENT=4026 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of t_mycat_syssql
-- ----------------------------

-- ----------------------------
-- Table structure for `t_mycat_syssqlhig`
-- ----------------------------
DROP TABLE IF EXISTS `t_mycat_syssqlhig`;
CREATE TABLE `t_mycat_syssqlhig` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `ID` int(11) NOT NULL,
  `DB_NAME` varchar(30) DEFAULT NULL,
  `AVG_TIME` int(11) DEFAULT NULL,
  `MAX_TIME` int(11) DEFAULT NULL,
  `EXECUTE_TIME` int(11) DEFAULT NULL,
  `FREQUENCY` int(11) DEFAULT NULL,
  `MIN_TIME` int(11) DEFAULT NULL,
  `LAST_TIME` bigint(15) DEFAULT NULL,
  `CONTENT` text,
  `LAST_TM` datetime DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of t_mycat_syssqlhig
-- ----------------------------

-- ----------------------------
-- Table structure for `t_mycat_syssqlslow`
-- ----------------------------
DROP TABLE IF EXISTS `t_mycat_syssqlslow`;
CREATE TABLE `t_mycat_syssqlslow` (
  `SID` int(11) NOT NULL DEFAULT '0',
  `DB_NAME` varchar(30) NOT NULL,
  `START_TIME` bigint(15) NOT NULL,
  `EXECUTE_TIME` int(11) NOT NULL,
  `USER` varchar(25) NOT NULL,
  `CONTENT` text NOT NULL,
  `START_TM` datetime NOT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of t_mycat_syssqlslow
-- ----------------------------

-- ----------------------------
-- Table structure for `t_mycat_syssqltable`
-- ----------------------------
DROP TABLE IF EXISTS `t_mycat_syssqltable`;
CREATE TABLE `t_mycat_syssqltable` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `DB_NAME` varchar(30) DEFAULT NULL,
  `DB_TABLE` varchar(255) NOT NULL,
  `RELACOUNT` varchar(255) DEFAULT NULL,
  `R` int(11) DEFAULT NULL,
  `PERCENT_R` varchar(15) DEFAULT NULL,
  `W` int(11) DEFAULT NULL,
  `RELATABLE` varchar(1000) DEFAULT NULL,
  `ID` int(11) DEFAULT NULL,
  `LAST_TIME` bigint(13) DEFAULT NULL,
  `LAST_TM` datetime DEFAULT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of t_mycat_syssqltable
-- ----------------------------
DROP TABLE IF EXISTS `t_mycat_sqlsum`;
CREATE TABLE `t_mycat_sqlsum` (
  `SID` int(11) NOT NULL AUTO_INCREMENT,
  `DB_NAME` varchar(30) DEFAULT NULL,
  `R` int(11) DEFAULT NULL,
  `PERCENT_R` varchar(15) DEFAULT NULL,
  `MAX` int(11) DEFAULT NULL,
  `W` int(11) DEFAULT NULL,
  `ID` int(11) DEFAULT NULL,
  `TIME_COUNT` varchar(255) DEFAULT NULL,
  `USER` varchar(25) DEFAULT NULL,
  `LAST_TIME` bigint(13) DEFAULT NULL,
  `LAST_TM` datetime DEFAULT NULL,
  `TTL_COUNT` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
