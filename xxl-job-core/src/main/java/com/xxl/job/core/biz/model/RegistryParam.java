package com.xxl.job.core.biz.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 */
public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private String registName;
    private String registryIp;
    private String registryPort;

    private List<JobHandleInfo> jobHandleInfos;
    private Integer[] jobs;

    public RegistryParam(){}
    public RegistryParam(String registName, String registryIp, String registryPort) {
        this.registName = registName;
        this.registryIp = registryIp;
        this.registryPort = registryPort;
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

    public String getRegistName() {
        return registName;
    }

    public void setRegistName(String registName) {
        this.registName = registName;
    }

    public String getRegistryIp() {
        return registryIp;
    }

    public void setRegistryIp(String registryIp) {
        this.registryIp = registryIp;
    }

    public String getRegistryPort() {
        return registryPort;
    }

    public void setRegistryPort(String registryPort) {
        this.registryPort = registryPort;
    }

    @Override
    public String toString() {
        return "RegistryParam{" +
                "registName='" + registName + '\'' +
                ", registryIp='" + registryIp + '\'' +
                ", registryPort='" + registryPort + '\'' +
                ", jobHandleInfos=" + jobHandleInfos +
                ", jobs=" + Arrays.toString(jobs) +
                '}';
    }
}
