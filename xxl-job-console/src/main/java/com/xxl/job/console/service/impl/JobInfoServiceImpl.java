package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.JobInfoDao;
import com.xxl.job.console.dao.JobInfoParamDao;
import com.xxl.job.console.model.JobInfo;
import com.xxl.job.console.service.JobInfoService;
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
 * @date: 2019/9/9
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {
    private static Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    @Resource
    JobInfoDao jobInfoDao;
    @Resource
    JobInfoParamDao jobInfoParamDao;

    @Override
    public int insert(JobInfo info) {
        try {
            return jobInfoDao.insert(info);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int update(JobInfo jobInfo) {
        try {
            return jobInfoDao.update(jobInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    @Transient
    public int delete(long id) {
        try {
            jobInfoParamDao.delete(id);
            return jobInfoDao.delete(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public JobInfo loadById(long id) {
        try {
            return jobInfoDao.loadById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
