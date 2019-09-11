package com.xxl.job.console.service;

import com.xxl.job.console.model.JobExecutorParam;
import com.xxl.job.console.model.JobInfoParam;
import com.xxl.job.console.model.JobLog;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/11
 */
public interface JobLogService {
    JobLog load(long id);

    List<Long> findFailJobLogIds(int pageSize);

    int save(JobLog jobLog);

    int insertParam(long logId,String jobInfoParams);

    List<JobInfoParam> queryExecutorParam(long logId,long jobId);

    int updateTriggerInfo(JobLog jobLog);

    int updateHandleInfo(JobLog jobLog);

    int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus);
}
