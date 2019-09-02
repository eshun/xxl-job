package com.xxl.job.console.service;

import com.xxl.job.console.model.Actuator;
import com.xxl.job.console.model.ActuatorParam;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
public interface ActuatorService {

    /**
     * 新增
     * @param actuator
     * @return
     */
    int insert(Actuator actuator);

    /**
     * 新增
     * @param actuator
     * @param appId
     * @return
     */
    int insert(Actuator actuator,Long appId);

    /**
     * 批量新增
     * @param actuatorParams
     * @return
     */
    int insertParams(List<ActuatorParam> actuatorParams);

    /**
     * 更新
     * @param actuator
     * @return
     */
    int update(Actuator actuator);

    /**
     * 禁用
     * @param appId
     * @param ids
     * @return
     */
    int disable(Long appId, Long[] ids);

    /**
     * 删除
     * @param actuatorId
     * @return
     */
    int deleteParam(Long actuatorId);

    /**
     * 根据执行器名称肯应用获取
     * @param name
     * @param appId
     * @return
     */
    Actuator loadByNameAndApp(String name,Long appId);
}
