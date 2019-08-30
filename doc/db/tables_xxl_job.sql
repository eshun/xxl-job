/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50725
Source Host           : localhost:3306
Source Database       : xxl_job

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-08-29 23:06:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for xxl_job_actuator
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_actuator`;
CREATE TABLE `xxl_job_actuator` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `NAME` varchar(200) NOT NULL COMMENT '执行器名称',
  `METHOD` varchar(200) NOT NULL COMMENT '执行器对应方法',
  `SERIAL_VERSION_UID` bigint(20) NOT NULL DEFAULT '0' COMMENT '序列化UID',
  `ROUTE_STRATEGY` varchar(200) NOT NULL DEFAULT 'FIRST' COMMENT '执行器路由策略,默认执行第一个',
  `PARAM_MD5` varchar(200) DEFAULT NULL COMMENT 'MD5',
  `RETURN_EXAMPLE` varchar(255) DEFAULT NULL,
  `IS_STATUS` int(4) NOT NULL DEFAULT '0' COMMENT '执行器状态，0正常1失效',
  `CREATE_TIME` datetime NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '修改时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_actuator_param
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_actuator_param`;
CREATE TABLE `xxl_job_actuator_param` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ACTUATOR_ID` bigint(20) NOT NULL COMMENT '执行器',
  `NAME` varchar(200) NOT NULL COMMENT '属性值',
  `VALUE` varchar(200) NOT NULL COMMENT '属性中文',
  `REQUIRED` int(4) NOT NULL DEFAULT '0' COMMENT '0非1必填',
  `CLASS_NAME` varchar(200) DEFAULT NULL COMMENT '属性ClassName',
  `DEFAULT_VALUE` varchar(200) DEFAULT NULL COMMENT '默认值',
  `PARAM_ORDER` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `PARAM_TYPE` int(11) NOT NULL DEFAULT '0' COMMENT '0入参 1出参',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_app
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_app`;
CREATE TABLE `xxl_job_app` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `NAME` varchar(200) NOT NULL COMMENT '应用名',
  `IP` varchar(200) NOT NULL COMMENT '应用IP',
  `PORT` int(10) NOT NULL DEFAULT '0' COMMENT '应用端口',
  `ONLINE` int(4) NOT NULL DEFAULT '1' COMMENT '0在线1不在线',
  `CREATE_TIME` datetime NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '修改时间',
  `JOB_INFO` varchar(200) DEFAULT NULL COMMENT '执行中任务数',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_app_actuator
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_app_actuator`;
CREATE TABLE `xxl_job_app_actuator` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ACTUATOR_ID` bigint(20) NOT NULL,
  `APP_ID` bigint(20) NOT NULL,
  `RES_TIME_MS` int(4) NOT NULL DEFAULT '0' COMMENT '平均响应时间（ms）',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) NOT NULL COMMENT '执行器名称',
  `order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
  `job_desc` varchar(255) NOT NULL,
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE `xxl_job_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
  `trigger_msg` text COMMENT '调度-日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int(11) NOT NULL COMMENT '执行-状态',
  `handle_msg` text COMMENT '执行-日志',
  `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE `xxl_job_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(255) NOT NULL,
  `registry_key` varchar(255) NOT NULL,
  `registry_value` varchar(255) NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE XXL_JOB_USER
(
    ID            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    USER_NAME     varchar(200) NOT NULL COMMENT '用户',
    PASSWORD varchar(200) NOT NULL COMMENT '密码',
    enabled   int(1)       NOT NULL DEFAULT 1 COMMENT '状态0正常1锁定',
    CREATE_TIME   datetime     NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '创建时间',
    UPDATE_TIME   datetime     NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '修改时间',
    PRIMARY KEY (ID)
);