package com.xxl.job.core.biz.model;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-13
*/
public class JobHandleInfo {

    public JobHandleInfo() {
    }

    private String name;

    private boolean cover;

    private String className;

    private String methodName;

    private String returnExample;

    private JobHandleParamInfo GenericReturn;

    private Long serialVersionUID;

    private List<JobHandleParamInfo> genericParameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCover() {
        return cover;
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setSerialVersionUID(Long serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
    }

    public String getReturnExample() {
        return returnExample;
    }

    public void setReturnExample(String returnExample) {
        this.returnExample = returnExample;
    }

    public JobHandleParamInfo getGenericReturn() {
        return GenericReturn;
    }

    public void setGenericReturn(JobHandleParamInfo genericReturn) {
        GenericReturn = genericReturn;
    }

    public List<JobHandleParamInfo> getGenericParameters() {
        return genericParameters;
    }

    public void setGenericParameters(List<JobHandleParamInfo> genericParameters) {
        this.genericParameters = genericParameters;
    }

    @Override
    public String toString() {
        return "JobHandleInfo{" +
                "name='" + name + '\'' +
                ", cover=" + cover +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnExample='" + returnExample + '\'' +
                ", GenericReturn=" + GenericReturn +
                ", serialVersionUID=" + serialVersionUID +
                ", genericParameters=" + genericParameters +
                '}';
    }
}
