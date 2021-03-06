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
public class ActuatorParam implements Serializable {
    private static final long serialVersionUID = 1L;

    public ActuatorParam() {
        this.id = SnowflakeId.getId();
        this.parentId = 0L;
        this.paramOrder = 0;
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 执行器
     */
    private Long actuatorId;

    /**
     * 父级
     */
    private Long parentId;

    /**
     * 属性值
     */
    private String name;

    /**
     * 属性中文
     */
    private String value;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 属性classname
     */
    private String className;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 排序
     */
    private Integer paramOrder;

    /**
     * 0入参 1出参
     */
    private Integer paramType;
}
