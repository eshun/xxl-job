package com.xxl.job.console.service.impl;

import com.google.common.reflect.TypeToken;
import com.xxl.job.console.dao.JobExecutorParamDao;
import com.xxl.job.console.dao.JobLogDao;
import com.xxl.job.console.model.JobExecutorParam;
import com.xxl.job.console.model.JobInfoParam;
import com.xxl.job.console.model.JobLog;
import com.xxl.job.console.service.JobLogService;
import com.xxl.job.core.util.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/11
 */
@Service
public class JobLogServiceImpl implements JobLogService {
    private static Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    @Resource
    JobLogDao jobLogDao;
    @Resource
    JobExecutorParamDao jobExecutorParamDao;

    @Override
    public JobLog load(long id) {
        try {
            return jobLogDao.load(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Long> findFailJobLogIds(int pageSize) {
        try {
            return jobLogDao.findFailJobLogIds(pageSize);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int save(JobLog jobLog) {
        try {
            return jobLogDao.save(jobLog);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    public int insertExecutorParam(long logId,List<JobExecutorParam> jobExecutorParams) {
        try {
            for (JobExecutorParam jobExecutorParam : jobExecutorParams) {
                List<JobExecutorParam> childs = jobExecutorParam.getChildren();
                if (childs != null) {
                    insertExecutorParam(logId,childs);
                }
            }
            return jobExecutorParamDao.insertList(logId,jobExecutorParams);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int insertParam(long logId,String jobInfoParams) {
        try {
            List<JobExecutorParam> jobExecutorParams = GsonUtil.fromJson(jobInfoParams, new TypeToken<List<JobExecutorParam>>() {
            }.getType());
            return insertExecutorParam(logId, jobExecutorParams);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public List<JobInfoParam> queryExecutorParam(long logId,long jobId) {

        List<JobExecutorParam> jobExecutorParams=jobExecutorParamDao.queryByLogId(logId);
        if(jobExecutorParams!=null){
            return mapping(jobExecutorParams,jobId,0L);
        }else{
            return null;
        }
    }

    private List<JobInfoParam> mapping(List<JobExecutorParam> jobExecutorParams,long jobId, long parentId) {
        List<JobInfoParam> jobInfoParams = new ArrayList<>();
        for (JobExecutorParam jobExecutorParam : jobExecutorParams) {
            if (parentId == jobExecutorParam.getParentId()) {
                JobInfoParam jobInfoParam = new JobInfoParam();
                BeanUtils.copyProperties(jobExecutorParam, jobInfoParam);
                jobInfoParam.setJobId(jobId);
                List<JobInfoParam> childs = mapping(jobExecutorParams, jobId, jobExecutorParam.getId());
                if (childs != null) {
                    jobInfoParam.setChildren(childs);
                }
                jobInfoParams.add(jobInfoParam);
            }
        }
        return jobInfoParams;
    }

    @Override
    public int updateTriggerInfo(JobLog jobLog) {
        try {
            return jobLogDao.updateTriggerInfo(jobLog);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int updateHandleInfo(JobLog jobLog) {
        try {
            return jobLogDao.updateHandleInfo(jobLog);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        try {
            return jobLogDao.updateAlarmStatus(logId, oldAlarmStatus, newAlarmStatus);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }
}
