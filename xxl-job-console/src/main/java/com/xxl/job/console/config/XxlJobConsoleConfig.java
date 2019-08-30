package com.xxl.job.console.config;

import com.xxl.job.console.dao.XxlJobInfoDao;
import com.xxl.job.console.dao.XxlJobLogDao;
import com.xxl.job.console.service.ActuatorService;
import com.xxl.job.console.service.AppService;
import com.xxl.job.core.biz.ConsoleBiz;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Component
public class XxlJobConsoleConfig implements InitializingBean{
    private static XxlJobConsoleConfig consoleConfig = null;
    public static XxlJobConsoleConfig getConsoleConfig() {
        return consoleConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consoleConfig = this;
    }

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${spring.mail.username}")
    private String emailUserName;

    /**
     * jwt密匙
     */
    @Value("${app.security.jwtSecret:'xxl-job'}")
    private String jwtSecret;

    /**
     * jwt有效期
     */
    @Value("${app.security.jwtExpirationInMs:86400000}")
    private Long jwtExpirationInMs;

    /**
     * 初始化管理员
     */
    @Value("${app.admin.name:'admin'}")
    private String initUser;

    // dao, service
    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private ConsoleBiz consoleBiz;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
    @Resource
    private AppService appService;
    @Resource
    private ActuatorService actuatorService;


    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public Long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public String getInitUser() {
        return initUser;
    }

    public XxlJobLogDao getXxlJobLogDao() {
        return xxlJobLogDao;
    }

    public XxlJobInfoDao getXxlJobInfoDao() {
        return xxlJobInfoDao;
    }

    public ConsoleBiz getConsoleBiz() {
        return consoleBiz;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public AppService getAppService() {
        return appService;
    }

    public ActuatorService getActuatorService() {
        return actuatorService;
    }
}
