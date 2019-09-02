package com.xxl.job.console.dao;

import com.xxl.job.console.model.ActuatorParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author esun
 * @version v1.0
 * @date: 2019/8/26
 */
@Mapper
public interface ActuatorParamDao {

    /**
     * 批量新增
     * @param actuatorParams
     * @return
     */
    int insertList(@Param("actuatorParams") List<ActuatorParam> actuatorParams);

    /**
     * 刪除
     * @param actuatorId
     * @return
     */
    int delete(@Param("actuatorId") Long actuatorId);
}
