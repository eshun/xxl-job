package com.xxl.job.core.executor.impl;

import com.xxl.job.core.biz.model.JobHandleInfo;
import com.xxl.job.core.biz.model.JobHandleParamInfo;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.Execute;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.Param;
import com.xxl.job.core.util.ReflectionUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware {


    @Override
    public void start() throws Exception {

        // init JobHandler Repository
        initJobHandlerRepository(applicationContext);

        // refresh GlueFactory
        GlueFactory.refreshInstance(1);


        // super start
        super.start();
    }

    private void initJobHandlerRepository(ApplicationContext applicationContext){
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
    }

    /**
     * 执行器类
     * @param jobHandler
     */
    private void initJobHandler(IJobHandler jobHandler){
        JobHandleInfo jobHandleInfo = new JobHandleInfo();

        Class<?> jobHandlerClass = jobHandler.getClass();
        JobHandler jobHandlerAnnotation = jobHandlerClass.getAnnotation(JobHandler.class);
        String name = jobHandlerAnnotation.value();
        boolean cover = jobHandlerAnnotation.cover();
        Long serialVersionUID = ReflectionUtil.getSerialVersionUID(jobHandlerClass);

        String className = jobHandlerClass.getName();
        String classShortName = ClassUtils.getShortName(className);

        jobHandleInfo.setClassName(classShortName);
        jobHandleInfo.setCover(cover);
        jobHandleInfo.setName(name);
        jobHandleInfo.setSerialVersionUID(serialVersionUID);

        // 执行器方法
        Method[] methods = jobHandlerClass.getDeclaredMethods();
        if(methods!=null&&methods.length>0)
        {
            List<Method> methodsList = new ArrayList<>();
            for (Method method : methods) {
                boolean hasExecute = method.isAnnotationPresent(Execute.class);
                // 只处理有Execute注解的方法
                if (hasExecute) {
                    methodsList.add(method);
                }
            }
            if (methodsList.size() > 0) {
                methodsList.sort((o1, o2) -> {
                    Order or1 = o1.getAnnotation(Order.class);
                    Order or2 = o2.getAnnotation(Order.class);
                    if (or1 == null && or2 == null) {
                        return 0;
                    } else if (or1 == null && or2 != null) {
                        return -1;
                    } else if (or1 != null && or2 == null) {
                        return 1;
                    } else {
                        return Integer.compare(or1.value(), or2.value());
                    }
                });
                // 只取第一个
                Method method = methodsList.get(0);
                jobHandleInfo.setMethodName(method.getName());
            }
        }
    }

    /**
     * 执行器参数
     * @param method
     */
    private void parameterNameDiscoverer(Method method){

    }

    /**
     * 执行器返回值
     * @param method
     */
    private List<JobHandleParamInfo> executeReturn(Method method) {
        List<JobHandleParamInfo> jobHandleParamInfos=new ArrayList<>();
        Class<?> returnType = method.getReturnType();
        String name=returnType.getName();
        String classShortName = ClassUtils.getShortName(name);

        if (returnType == void.class || returnType == Void.class) {
            return null;
        } else if (returnType.isPrimitive()) {
            JobHandleParamInfo jobHandleParamInfo=new JobHandleParamInfo();
            jobHandleParamInfo.setName(classShortName);
            jobHandleParamInfo.setClassName(name);
            Execute executeAnnotation = method.getAnnotation(Execute.class);
            if(executeAnnotation!=null) {
                Param param = executeAnnotation.param();
                jobHandleParamInfo.setValue(param.value());
            }
            Param paramAnnotation=method.getAnnotation(Param.class);
            if(paramAnnotation!=null) {
                Param param = executeAnnotation.param();
                jobHandleParamInfo.setValue(param.value());
            }
            jobHandleParamInfos.add(jobHandleParamInfo);
        } else if (returnType.isArray()) {
            Class<?>[] returnTypes=returnType.getClasses();


        } else if (Collection.class.isAssignableFrom(returnType)) {

        } else if (Map.class.isAssignableFrom(returnType)) {

        } else{
            Field[] fields = returnType.getDeclaredFields();
            for (Field field : fields) {

            }
        }
    }

    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobSpringExecutor.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
