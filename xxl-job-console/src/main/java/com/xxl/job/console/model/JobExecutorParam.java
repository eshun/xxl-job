package com.xxl.job.console.model;

import com.xxl.job.console.core.util.SnowflakeId;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019/9/9
 */
@Data
public class JobExecutorParam {
    public JobExecutorParam() {
        this.id = SnowflakeId.getId();
        this.parentId = 0L;
        this.paramOrder = 0;
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 日志
     */
    private Long logId;

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
     * 任务值
     */
    private String jobValue;

    /**
     * 属性classname
     */
    private String className;

    /**
     * 排序
     */
    private Integer paramOrder;

    /**
     * children
     */
    private List<JobExecutorParam> children;
}
