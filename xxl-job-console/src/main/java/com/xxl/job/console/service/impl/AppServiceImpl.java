package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.AppActuatorDao;
import com.xxl.job.console.dao.AppDao;
import com.xxl.job.console.model.App;
import com.xxl.job.console.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.List;

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

    @Override
    @Transient
    public int insert(App app) {
        try {
            return appDao.insert(app);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    @Transient
    public int delete(Long id) {
        try {
            appActuatorDao.delete(id);
            return appDao.delete(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    @Transient
    public int update(App app) {
        try {
            return appDao.update(app);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int offline(int timeout) {
        try {
            return appDao.offline(timeout);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public App load(Long id) {
        try {
            return appDao.load(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public App loadBy(String name, String ip, Integer port) {
        try {
            return appDao.loadBy(name, ip, port);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<App> query(int offset, int pageSize) {
        try {
            return appDao.query(offset,pageSize);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int count(int offset, int pageSize) {
        try {
            return appDao.count(offset,pageSize);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public List<App> queryByActuatorId(Long actuatorId) {
        try {
            return appDao.queryByActuatorId(actuatorId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
