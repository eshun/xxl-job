package com.xxl.job.core.biz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-13
*/
public class JobHandleParamInfo implements Serializable {
    public JobHandleParamInfo() {
    }

    private String name;

    private String value;

    private String className;

    private String defaultValue;

    private boolean required;

    /**
     * 0入参 1出参
     */
    private int ParamType;

    private int ParamOrder;

    private String jobValue;

    private List<JobHandleParamInfo> children;

    public List<JobHandleParamInfo> getChildren() {
        return children;
    }

    public void setChildren(List<JobHandleParamInfo> children) {
        this.children = children;
    }

    public void addChildren(JobHandleParamInfo jobHandleParamInfo){
        if(this.children==null){
            this.children=new ArrayList<>();
        }
        this.children.add(jobHandleParamInfo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getParamType() {
        return ParamType;
    }

    public void setParamType(int paramType) {
        ParamType = paramType;
    }

    public int getParamOrder() {
        return ParamOrder;
    }

    public void setParamOrder(int paramOrder) {
        ParamOrder = paramOrder;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getJobValue() {
        return jobValue;
    }

    public void setJobValue(String jobValue) {
        this.jobValue = jobValue;
    }

    @Override
    public String toString() {
        return "JobHandleParamInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", className='" + className + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", required=" + required +
                ", ParamType=" + ParamType +
                ", ParamOrder=" + ParamOrder +
                ", jobValue='" + jobValue + '\'' +
                ", children=" + children +
                '}';
    }
}
