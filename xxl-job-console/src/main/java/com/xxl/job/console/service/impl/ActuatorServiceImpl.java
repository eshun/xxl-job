package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.ActuatorDao;
import com.xxl.job.console.dao.ActuatorParamDao;
import com.xxl.job.console.dao.AppActuatorDao;
import com.xxl.job.console.model.Actuator;
import com.xxl.job.console.model.AppActuator;
import com.xxl.job.console.service.ActuatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.Transient;

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
    ActuatorDao actuatorDao;

    @Resource
    ActuatorParamDao actuatorParamDao;

    @Resource
    AppActuatorDao appActuatorDao;

    @Override
    public int insert(Actuator actuator) {
        try{
            return actuatorDao.insert(actuator);
        }catch (Exception e){
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    @Transient
    public int insert(Actuator actuator, Long appId) {
        try{
            insert(actuator);

            AppActuator appActuator=new AppActuator();
            appActuator.setActuatorId(actuator.getId());
            appActuator.setAppId(appId);
            return appActuatorDao.insert(appActuator);
        }catch (Exception e){
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int update(Actuator actuator) {
        try{
            return actuatorDao.update(actuator);
        }catch (Exception e){
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int disable(Long appId, Long[] ids) {
        try {
            return actuatorDao.disable(appId, ids);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int deleteParam(Long actuatorId) {
        try{
            return actuatorParamDao.delete(actuatorId);
        }catch (Exception e){
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public Actuator loadByNameAndApp(String name, Long appId) {
        try{
            return actuatorDao.loadByNameAndApp(name,appId);
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }
}
