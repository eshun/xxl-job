package com.xxl.job.console.dao;

import com.xxl.job.console.model.ActuatorParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author esun
 * @version v1.0
 * @date: 2019/8/26
 */
@Mapper
public interface ActuatorParamDao {

    /**
     * [新增]
     * @author esun
     * @date 2019/08/26
     **/
    int insert(ActuatorParam actuatorParam);

    /**
     * [刪除]
     * @author esun
     * @date 2019/08/26
     **/
    int delete(@Param("actuatorId") Long actuatorId);
}
