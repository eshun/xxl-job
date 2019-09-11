package com.xxl.job.core.biz.model;

import com.google.gson.reflect.TypeToken;
import com.xxl.job.core.util.GsonUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private long jobId;

    private String executorHandler;
    private String executorBlockStrategy;
    private int executorTimeout;

    private String executorMethod;

    private long logId;
    private long logDateTim;

    private int broadcastIndex;
    private int broadcastTotal;

    private List<JobHandleParamInfo> executorParams;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public List<JobHandleParamInfo> getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(List<JobHandleParamInfo> executorParams) {
        this.executorParams = executorParams;
    }

    public void setExecutorParams(String executorParams) {
        try {
            List<JobHandleParamInfo> jobExecutorParams = GsonUtil.fromJson(executorParams, new TypeToken<List<JobHandleParamInfo>>() {
            }.getType());
            this.executorParams = jobExecutorParams;
        } catch (Exception e) {
        }
    }

    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }

    public int getExecutorTimeout() {
        return executorTimeout;
    }

    public void setExecutorTimeout(int executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    public String getExecutorMethod() {
        return executorMethod;
    }

    public void setExecutorMethod(String executorMethod) {
        this.executorMethod = executorMethod;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public int getBroadcastIndex() {
        return broadcastIndex;
    }

    public void setBroadcastIndex(int broadcastIndex) {
        this.broadcastIndex = broadcastIndex;
    }

    public int getBroadcastTotal() {
        return broadcastTotal;
    }

    public void setBroadcastTotal(int broadcastTotal) {
        this.broadcastTotal = broadcastTotal;
    }


    @Override
    public String toString() {
        return "TriggerParam{" +
                "jobId=" + jobId +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParams='" + executorParams + '\'' +
                ", executorBlockStrategy='" + executorBlockStrategy + '\'' +
                ", executorTimeout=" + executorTimeout +
                ", logId=" + logId +
                ", logDateTim=" + logDateTim +
                ", broadcastIndex=" + broadcastIndex +
                ", broadcastTotal=" + broadcastTotal +
                '}';
    }

}
