package com.xxl.job.console.model;

import com.xxl.job.console.core.util.SnowflakeId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author esun
 * @version v1.0
 * @date: 2019/8/26
 */
@Data
public class AppActuator implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * actuator_id
     */
    private Long actuatorId;

    /**
     * app_id
     */
    private Long appId;

    /**
     * 平均响应时间（ms）
     */
    private Integer resTimeMs;

    public AppActuator() {
        this.id = SnowflakeId.getId();
        this.resTimeMs = 0;
    }

}
