package com.xxl.job.core.biz.model;

import java.io.Serializable;

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

    /**
     * 0入参 1出参
     */
    private int ParamType;

    private int ParamOrder;

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

    @Override
    public String toString() {
        return "JobHandleParamInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", className='" + className + '\'' +
                ", ParamType=" + ParamType +
                ", ParamOrder=" + ParamOrder +
                '}';
    }
}
