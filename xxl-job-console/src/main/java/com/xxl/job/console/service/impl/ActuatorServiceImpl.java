package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.ActuatorDao;
import com.xxl.job.console.dao.ActuatorParamDao;
import com.xxl.job.console.service.ActuatorService;
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
public class ActuatorServiceImpl implements ActuatorService {
    private static Logger logger = LoggerFactory.getLogger(ActuatorServiceImpl.class);

    @Resource
    private ActuatorDao actuatorDao;

    @Resource
    private ActuatorParamDao actuatorParamDao;
}
