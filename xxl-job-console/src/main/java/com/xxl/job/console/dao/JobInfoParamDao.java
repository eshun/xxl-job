package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobInfoParam;
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
public interface JobInfoParamDao {

    /**
     * 批量新增
     * @param jobInfoParams
     * @return
     */
    int insertList(@Param("JobInfoParams") List<JobInfoParam> jobInfoParams);

    /**
     * 刪除
     * @param jobId
     * @return
     */
    int delete(@Param("jobId") Long jobId);
}
