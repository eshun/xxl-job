package com.xxl.job.core.executor.impl;

import com.xxl.job.core.biz.model.JobHandleInfo;
import com.xxl.job.core.biz.model.JobHandleParamInfo;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.Execute;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.Param;
import com.xxl.job.core.util.GsonUtil;
import com.xxl.job.core.util.ReflectionUtil;
import jdk.nashorn.internal.runtime.Debug;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

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
                    initJobHandler(handler);
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
    private void initJobHandler(IJobHandler jobHandler) {
        JobHandleInfo jobHandleInfo = new JobHandleInfo();

        Class<?> jobHandlerClass = jobHandler.getClass();
        JobHandler jobHandlerAnnotation = jobHandlerClass.getAnnotation(JobHandler.class);
        String name = jobHandlerAnnotation.value();
        boolean cover = jobHandlerAnnotation.cover();
        Long serialVersionUID = ReflectionUtil.getSerialVersionUID(jobHandlerClass);

        String className = jobHandlerClass.getName();
        String classShortName = ClassUtils.getShortName(className);

        System.out.println("initJobHandler=>");
        // 执行器方法
        Method[] methods = jobHandlerClass.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
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

                jobHandleInfo.setClassName(classShortName);
                jobHandleInfo.setCover(cover);
                jobHandleInfo.setName(name);
                jobHandleInfo.setSerialVersionUID(serialVersionUID);
                jobHandleInfo.setMethodName(method.getName());

                Execute executeAnnotation = method.getAnnotation(Execute.class);

                //Class<?> returnType = method.getReturnType();
                Type returnType=method.getGenericReturnType();

                if(returnType != null) {
                    JobHandleParamInfo jobHandleParamInfo = executeReturn(returnType, executeAnnotation, null);

                    System.out.println(jobHandleParamInfo);
                    //jobHandleInfo.setJobHandleParamInfos(jobHandleParamInfos);
                }
            }
        }
    }

    /**
     * 执行器参数
     * @param method
     */
    private void parameterNameDiscoverer(Method method) {
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();

    }

    /**
     * 执行器返回值
     * @param type
     * @param execute
     * @return
     */
    private JobHandleParamInfo executeReturn(Type type,Execute execute,Field field) {

        Class<?> clazz = ParameterizedType.class.isAssignableFrom(type.getClass()) ?
                ((ParameterizedTypeImpl) type).getRawType() : (Class<?>) type;
        String clazzName = clazz.getName();
        String name = ClassUtils.getShortName(clazzName);
        if (field != null) {
            name = field.getName();
        }
        JobHandleParamInfo jobHandleParamInfo = new JobHandleParamInfo();
        jobHandleParamInfo.setParamType(1);
        jobHandleParamInfo.setName(name);
        jobHandleParamInfo.setClassName(clazzName);
        if (clazz.isArray()) {
            jobHandleParamInfo.setName("Array");
            jobHandleParamInfo.setClassName("Array");

            Class<?> newClass= clazz.getComponentType();
            jobHandleParamInfo.addChildren(executeReturn(newClass, null, null));
        } else if (clazz == void.class || clazz == Void.class) {
            return null;
        } else if (ReflectionUtil.isPrimitive(clazzName)) {
            if (execute != null) {
                Param param = execute.param();
                jobHandleParamInfo.setValue(param.value());
            }
            if (field != null) {
                Param paramAnnotation = field.getAnnotation(Param.class);
                if (paramAnnotation != null) {
                    jobHandleParamInfo.setValue(paramAnnotation.value());
                }
            }
        }  else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                for (Type newType : types) {
                    jobHandleParamInfo.addChildren(executeReturn(newType, null, null));
                }
            }
        } else {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                Class<?> fieldClass = f.getType();
                int modifiers = f.getModifiers();
                Type genericType = f.getGenericType();
                if (genericType == null || Modifier.isStatic(modifiers) || Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                    continue;
                }
                if (genericType.getTypeName().equals("T") && type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
                jobHandleParamInfo.addChildren(executeReturn(fieldClass, null, f));
            }
        }
        return jobHandleParamInfo;
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
