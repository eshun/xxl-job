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
public class App implements Serializable {
    private static final long serialVersionUID = 1L;

    public App() {
        this.id= SnowflakeId.getId();
        this.online = 1;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 应用名
     */
    private String name;

    /**
     * 应用ip
     */
    private String ip;

    /**
     * 应用端口
     */
    private Integer port;

    /**
     * 0在线1不在线
     */
    private Integer online;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 执行中任务数
     */
    private String jobInfo;
}
