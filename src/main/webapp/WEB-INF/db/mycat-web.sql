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
`ID`  int(11) NOT NULL DEFAULT 0 ,
`START_TIME`  bigint(15) NOT NULL ,
`EXECUTE_TIME`  int(11) NOT NULL ,
`USER`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`START_TM`  datetime NOT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=COMPACT
;

DROP TABLE IF EXISTS `t_mycat_syssqlhig`;
CREATE TABLE `t_mycat_syssqlhig` (
`ID`  int(11) NOT NULL ,
`AVG_TIME`  int(11) NULL DEFAULT NULL ,
`MAX_TIME`  int(11) NULL DEFAULT NULL ,
`EXECUTE_TIME`  int(11) NULL DEFAULT NULL ,
`FREQUENCY`  int(11) NULL DEFAULT NULL ,
`MIN_TIME`  int(11) NULL DEFAULT NULL ,
`LAST_TIME`  bigint(15) NULL DEFAULT NULL ,
`CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`LAST_TM`  datetime NULL DEFAULT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=COMPACT
;

DROP TABLE IF EXISTS `t_mycat_syssqlslow`;
CREATE TABLE `t_mycat_syssqlslow` (
`START_TIME`  bigint(15) NOT NULL ,
`EXECUTE_TIME`  int(11) NOT NULL ,
`USER`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`START_TM`  datetime NOT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=COMPACT
;

DROP TABLE IF EXISTS `t_mycat_syssqltable`;
CREATE TABLE `t_mycat_syssqltable` (
`DB_TABLE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`RELACOUNT`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`R`  int(11) NULL DEFAULT NULL ,
`PERCENT_R`  varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`W`  int(11) NULL DEFAULT NULL ,
`RELATABLE`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ID`  int(11) NULL DEFAULT NULL ,
`LAST_TIME`  bigint(13) NULL DEFAULT NULL ,
`LAST_TM`  datetime NULL DEFAULT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=COMPACT
;


 