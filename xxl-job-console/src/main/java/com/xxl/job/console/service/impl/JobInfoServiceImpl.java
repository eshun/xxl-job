package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.JobInfoDao;
import com.xxl.job.console.dao.JobInfoParamDao;
import com.xxl.job.console.model.JobInfo;
import com.xxl.job.console.model.JobInfoParam;
import com.xxl.job.console.service.JobInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

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
    @Transient
    public int insert(JobInfo info) {
        try {
            if(info.getJobInfoParams()!=null){
                jobInfoParamDao.insertList(info.getJobInfoParams());
            }
            return jobInfoDao.insert(info);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int update(JobInfo jobInfo) {
        try {
            if(jobInfo.getJobInfoParams()!=null){
                jobInfoParamDao.delete(jobInfo.getId());
                jobInfoParamDao.insertList(jobInfo.getJobInfoParams());
            }
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

    @Override
    public List<JobInfoParam> queryByParent(long jobId, long parentId) {
        try {
            return jobInfoParamDao.queryByParent(jobId, parentId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<JobInfoParam> queryByParent(long jobId) {
        return eachInfoParam(jobId, 0);
    }

    @Override
    public List<JobInfo> scheduleJobQuery(long maxNextTime) {
        try {
            return jobInfoDao.scheduleJobQuery(maxNextTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int scheduleUpdate(JobInfo jobInfo) {
        try {
            return jobInfoDao.scheduleUpdate(jobInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    private List<JobInfoParam> eachInfoParam(long jobId, long parentId){
        List<JobInfoParam> jobInfoParams = queryByParent(jobId, parentId);
        if (jobInfoParams != null) {
            List<JobInfoParam> newJobInfoParams =new ArrayList<>();
            for (JobInfoParam jobInfoParam : jobInfoParams) {
                List<JobInfoParam> childs = eachInfoParam(jobId, jobInfoParam.getId());
                jobInfoParam.setChildren(childs);
                newJobInfoParams.add(jobInfoParam);
            }
            return newJobInfoParams;
        }
        return null;
    }
}
