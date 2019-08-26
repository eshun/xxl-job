package com.xxl.job.core.biz.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 */
public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private String registGroup;
    private String registryKey;
    private String registryValue;

    List<JobHandleInfo> jobHandleInfos;
    Integer[] jobs;

    public RegistryParam(){}
    public RegistryParam(String registGroup, String registryKey, String registryValue) {
        this.registGroup = registGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
    }

    public List<JobHandleInfo> getJobHandleInfos() {
        return jobHandleInfos;
    }

    public void setJobHandleInfos(List<JobHandleInfo> jobHandleInfos) {
        this.jobHandleInfos = jobHandleInfos;
    }

    public Integer[] getJobs() {
        return jobs;
    }

    public void setJobs(Integer[] jobs) {
        this.jobs = jobs;
    }

    public String getRegistGroup() {
        return registGroup;
    }

    public void setRegistGroup(String registGroup) {
        this.registGroup = registGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    @Override
    public String toString() {
        return "RegistryParam{" +
                "registGroup='" + registGroup + '\'' +
                ", registryKey='" + registryKey + '\'' +
                ", registryValue='" + registryValue + '\'' +
                ", jobHandleInfos=" + jobHandleInfos +
                ", jobs=" + Arrays.toString(jobs) +
                '}';
    }
}
