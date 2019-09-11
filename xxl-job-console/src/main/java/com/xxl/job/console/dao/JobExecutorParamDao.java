package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobExecutorParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/9
 */
@Mapper
public interface JobExecutorParamDao {

    /**
     * 批量新增
     * @param logId
     * @param jobExecutorParams
     * @return
     */
    int insertList(@Param("logId") long logId,@Param("JobExecutorParams") List<JobExecutorParam> jobExecutorParams);

    /**
     * 刪除
     * @param logId
     * @return
     */
    int delete(@Param("logId") long logId);

    /**
     * 获取任务参数
     * @param logId
     * @return
     */
    List<JobExecutorParam> queryByLogId(@Param("logId") long logId);
}
