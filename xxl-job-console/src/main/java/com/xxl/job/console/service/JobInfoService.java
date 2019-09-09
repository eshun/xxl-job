package com.xxl.job.console.service;

import com.xxl.job.console.model.JobInfo;
import com.xxl.job.console.model.JobInfoParam;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/9
 */
public interface JobInfoService {

    /**
     * 新增
     * @param info
     * @return
     */
    int insert(JobInfo info);

    /**
     * 修改
     * @param jobInfo
     * @return
     */
    int update(JobInfo jobInfo);

    /**
     * 删除
     * @param id
     * @return
     */
    int delete(long id);

    /**
     * 根据Id获取任务
     * @param id
     * @return
     */
    JobInfo loadById(long id);

    /**
     * 获取任务参数
     * @param jobId
     * @param parentId
     * @return
     */
    List<JobInfoParam> queryByParent(long jobId, long parentId);
}
