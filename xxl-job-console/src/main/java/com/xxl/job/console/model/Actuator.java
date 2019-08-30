package com.xxl.job.console.model;

import com.xxl.job.console.core.util.SnowflakeId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author esun
 * @version v1.0
 * @date: 2019/8/26
 */
@Data
public class Actuator implements Serializable {
    private static final long serialVersionUID = 1L;

    public Actuator() {
        this.id= SnowflakeId.getId();
        this.status = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 执行器名称
     */
    private String name;

    /**
     * 执行器对应方法
     */
    private String method;

    /**
     * 序列化uid
     */
    private Long serialVersionUid;

    /**
     * 执行器路由策略，默认执行第一个
     */
    private String routeStrategy;

    /**
     * md5
     */
    private String paramMd5;

    /**
     * return_example
     */
    private String returnExample;

    /**
     * 执行器状态，0正常1失效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
