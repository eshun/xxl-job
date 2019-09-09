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
  `SERIAL_VERSION_UID` bigint(20) COMMENT '序列化UID',
  `ROUTE_STRATEGY` varchar(200) NOT NULL DEFAULT 'FIRST' COMMENT '执行器路由策略,默认执行第一个',
  `PARAM_MD5` varchar(200) COMMENT 'MD5',
  `RETURN_EXAMPLE` varchar(255) COMMENT 'EXAMPLE',
  `STATUS` int(4) NOT NULL DEFAULT '0' COMMENT '执行器状态，0正常1失效',
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
  `PARENT_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '父级',
  `NAME` varchar(200) NOT NULL COMMENT '属性值',
  `VALUE` varchar(200) NOT NULL DEFAULT '' COMMENT '属性中文',
  `REQUIRED` int(4) NOT NULL DEFAULT '0' COMMENT '0非1必填',
  `CLASS_NAME` varchar(200) DEFAULT NULL COMMENT '属性ClassName',
  `DEFAULT_VALUE` varchar(200) DEFAULT '' COMMENT '默认值',
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
  `JOB_INFO` varchar(200) DEFAULT '' COMMENT '执行中任务数',
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
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actuator_id` bigint(20) NOT NULL COMMENT '执行器,主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
  `job_desc` varchar(255) NOT NULL,
  `create_time` datetime  DEFAULT '2019-06-01 00:00:00' COMMENT '创建时间',
  `update_time` datetime  DEFAULT '2019-06-01 00:00:00' COMMENT '修改时间',
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_info_param
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info_param`;
CREATE TABLE `xxl_job_info_param` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `JOB_ID` bigint(20) NOT NULL COMMENT '任务',
  `PARENT_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '父级',
  `NAME` varchar(200) NOT NULL COMMENT '属性值',
  `VALUE` varchar(200) NOT NULL DEFAULT '' COMMENT '属性中文',
  `JOB_VALUE` varchar(200) DEFAULT '' COMMENT '任务值',
  `CLASS_NAME` varchar(200) NOT NULL COMMENT '属性ClassName',
  `PARAM_ORDER` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `actuator_id` bigint(20) NOT NULL COMMENT '执行器,主键ID',
  `job_id` bigint(20) NOT NULL COMMENT '任务，主键ID',
  `app_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用,主键ID',
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
  KEY `i_trigger_time` (`trigger_time`),
  KEY `i_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user` (
    `ID`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_NAME`     varchar(200) NOT NULL COMMENT '用户',
    `PASSWORD` varchar(200) NOT NULL COMMENT '密码',
    `ENABLED`   int(1)       NOT NULL DEFAULT '1' COMMENT '状态0正常1锁定',
    `CREATE_TIME`   datetime     NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '创建时间',
    `UPDATE_TIME`   datetime     NOT NULL DEFAULT '2019-06-01 00:00:00' COMMENT '修改时间',
    PRIMARY KEY (`ID`),
    UNIQUE KEY `I_USER_NAME` (`USER_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
