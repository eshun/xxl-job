package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.AppActuatorDao;
import com.xxl.job.console.dao.AppDao;
import com.xxl.job.console.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
@Service
public class AppServiceImpl implements AppService {
    private static Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    @Resource
    private AppDao appDao;

    @Resource
    private AppActuatorDao appActuatorDao;
}
