package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.JobHandleInfo;
import com.xxl.job.core.biz.model.JobHandleParamInfo;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.Execute;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.Param;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.job.core.util.GsonUtil;
import com.xxl.job.core.util.PojoToJsonUtil;
import com.xxl.job.core.util.ReflectionUtil;
import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.IpUtil;
import com.xxl.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    // ---------------------- param ----------------------
    private String adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath;
    private int logRetentionDays;

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }


    // ---------------------- start + stop ----------------------
    public void start() throws Exception {

        // init logpath
        XxlJobFileAppender.initLogPath(logPath);

        // init invoker, admin-client
        initAdminBizList(adminAddresses, accessToken);


        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);

        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();
        initRpcProvider(ip, port, appName, accessToken);
    }

    public void destroy() {
        // destory jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : jobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();


        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();

        // destory TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();

        // destory executor-server
        stopRpcProvider();

        // destory invoker
        stopInvokerFactory();
    }


    // ---------------------- admin-client (rpc invoker) ----------------------
    private static List<AdminBiz> adminBizList;
    private static Serializer serializer;

    private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        serializer = Serializer.SerializeEnum.HESSIAN.getSerializer();
        if (adminAddresses != null && adminAddresses.trim().length() > 0) {
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && address.trim().length() > 0) {

                    String addressUrl = address.concat(AdminBiz.MAPPING);

                    AdminBiz adminBiz = (AdminBiz) new XxlRpcReferenceBean(
                            NetEnum.NETTY_HTTP,
                            serializer,
                            CallType.SYNC,
                            LoadBalance.ROUND,
                            AdminBiz.class,
                            null,
                            3000,
                            addressUrl,
                            accessToken,
                            null,
                            null
                    ).getObject();

                    if (adminBizList == null) {
                        adminBizList = new ArrayList<AdminBiz>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    private void stopInvokerFactory() {
        // stop invoker factory
        try {
            XxlRpcInvokerFactory.getInstance().stop();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static List<AdminBiz> getAdminBizList() {
        return adminBizList;
    }

    public static Serializer getSerializer() {
        return serializer;
    }


    // ---------------------- executor-server (rpc provider) ----------------------
    private XxlRpcProviderFactory xxlRpcProviderFactory = null;

    private void initRpcProvider(String ip, int port, String appName, String accessToken) throws Exception {

        // init, provider factory
        String address = IpUtil.getIpPort(ip, port);
        Map<String, String> serviceRegistryParam = new HashMap<String, String>();
        serviceRegistryParam.put("appName", appName);
        serviceRegistryParam.put("address", address);

        xxlRpcProviderFactory = new XxlRpcProviderFactory();
        xxlRpcProviderFactory.initConfig(NetEnum.NETTY_HTTP, Serializer.SerializeEnum.HESSIAN.getSerializer(), ip, port, accessToken, ExecutorServiceRegistry.class, serviceRegistryParam);

        // add services
        xxlRpcProviderFactory.addService(ExecutorBiz.class.getName(), null, new ExecutorBizImpl());

        // start
        xxlRpcProviderFactory.start();

    }

    public static class ExecutorServiceRegistry extends ServiceRegistry {

        @Override
        public void start(Map<String, String> param) {
            // start registry
            ExecutorRegistryThread.getInstance().start(param.get("appName"), param.get("address"));
        }

        @Override
        public void stop() {
            // stop registry
            ExecutorRegistryThread.getInstance().toStop();
        }

        @Override
        public boolean registry(Set<String> keys, String value) {
            return false;
        }

        @Override
        public boolean remove(Set<String> keys, String value) {
            return false;
        }

        @Override
        public Map<String, TreeSet<String>> discovery(Set<String> keys) {
            return null;
        }

        @Override
        public TreeSet<String> discovery(String key) {
            return null;
        }

    }

    private void stopRpcProvider() {
        // stop provider factory
        try {
            xxlRpcProviderFactory.stop();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- job handler repository ----------------------
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }


    // ---------------------- JobHandleInfo ----------------------
    public static List<JobHandleInfo> getJobHandleInfos() {
        List<JobHandleInfo> jobHandleInfos = new ArrayList<>();
        for (IJobHandler jobHandler : jobHandlerRepository.values()) {
            try {
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
                                return 1;
                            } else if (or1 != null && or2 == null) {
                                return -1;
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

                        //Class<?> returnClass = method.getReturnType();
                        Type returnType = ReflectionUtil.getReturnTypes(method)[1];

                        if (returnType != null) {
                            JobHandleParamInfo jobHandleParamInfo = executeReturn(returnType, executeAnnotation, null);
                            jobHandleInfo.setGenericReturn(jobHandleParamInfo);

                            Object object = PojoToJsonUtil.pojoToJson(returnType, executeAnnotation, null);
                            String json = GsonUtil.toJson(object);
                            jobHandleInfo.setReturnExample(json);
                        }

                        List<JobHandleParamInfo> jobHandleParamInfos = parameterNameDiscoverer(method);
                        if (jobHandleParamInfos != null && jobHandleParamInfos.size() > 0) {
                            jobHandleInfo.setGenericParameters(jobHandleParamInfos);
                        }

                        jobHandleInfos.add(jobHandleInfo);
                    }
                }
            } catch (Exception e) {

            }
        }
        return jobHandleInfos;
    }

    /**
     * 执行器参数
     *
     * @param method
     */
    private static List<JobHandleParamInfo> parameterNameDiscoverer(Method method) {
        List<JobHandleParamInfo> jobHandleParamInfos = new ArrayList<>();
        Type[] types = method.getGenericParameterTypes();//getParameterTypes()
        if (types.length > 0) {
            ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

            Annotation[][] annotations = method.getParameterAnnotations();
            int i = 0;
            for (Type t : types) {
                Class<?> clazz = (Class<?>) t;
                JobHandleParamInfo jobHandleParamInfo = new JobHandleParamInfo();
                jobHandleParamInfo.setParamType(0);
                jobHandleParamInfo.setName(paramNames[i]);
                jobHandleParamInfo.setClassName(clazz.getName());
                jobHandleParamInfo.setParamOrder(i);
                Annotation[] paramAnnotations = annotations[i];
                if (paramAnnotations.length > 0) {
                    Annotation annotation = paramAnnotations[0];
                    if (annotation != null) {
                        if (annotation.annotationType().equals(Param.class)) {
                            try {
                                jobHandleParamInfo.setValue(((Param) annotation).value());
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                if (clazz.isArray()) {
                    jobHandleParamInfo.setClassName("Array");
                    Class<?> newClass = clazz.getComponentType();
                    jobHandleParamInfo.addChildren(getParamInfo(newClass, null));
                } else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
                    if (t instanceof ParameterizedType) {
                        Type[] parameterizedTypes = ((ParameterizedType) t).getActualTypeArguments();
                        for (Type newType : parameterizedTypes) {
                            jobHandleParamInfo.addChildren(getParamInfo(newType, null));
                        }
                    }
                } else {
                    jobHandleParamInfo.setChildren(getFieldParamInfo(t));
                }
                jobHandleParamInfos.add(jobHandleParamInfo);
                i++;
            }
        }
        return jobHandleParamInfos;
    }

    /**
     * @param t
     * @param field
     * @return
     */
    private static JobHandleParamInfo getParamInfo(Type t, Field field) {
        JobHandleParamInfo jobHandleParamInfo = new JobHandleParamInfo();
        Class<?> clazz = (Class<?>) t;
        String clazzName = clazz.getName();
        String name = ClassUtils.getShortName(clazzName);
        if (field != null) {
            name = field.getName();
        }
        jobHandleParamInfo.setParamType(0);
        jobHandleParamInfo.setName(name);
        jobHandleParamInfo.setClassName(clazzName);
        if (field != null) {
            Param paramAnnotation = field.getAnnotation(Param.class);
            if (paramAnnotation != null) {
                jobHandleParamInfo.setValue(paramAnnotation.value());
            }
        }
        if (clazz.isArray()) {
            jobHandleParamInfo.setName("Array");
            jobHandleParamInfo.setClassName("Array");
            Class<?> newClass = clazz.getComponentType();
            jobHandleParamInfo.addChildren(getParamInfo(newClass, field));
        } else if (ReflectionUtil.isPrimitive(clazz)) {

        } else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            if (t instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) t).getActualTypeArguments();
                for (Type newType : types) {
                    jobHandleParamInfo.addChildren(getParamInfo(newType, field));
                }
            }
        } else {
            jobHandleParamInfo.setChildren(getFieldParamInfo(t));
        }
        return jobHandleParamInfo;
    }

    /**
     * @param t
     * @return
     */
    private static List<JobHandleParamInfo> getFieldParamInfo(Type t) {
        List<JobHandleParamInfo> jobHandleParamInfos = new ArrayList<>();
        Class<?> clazz = (Class<?>) t;
        Map<String, Method> mapMethods = ReflectionUtil.getBeanPropertyReadMethods(clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            Class<?> fieldClass = f.getType();
            Type genericType = f.getGenericType();
            if (!mapMethods.containsKey(f.getName())) {
                continue;
            }
            if (genericType.getTypeName().equals("T") && t instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) t;
                fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
            jobHandleParamInfos.add(getParamInfo(fieldClass, f));
        }
        return jobHandleParamInfos;
    }

    /**
     * 执行器返回值
     *
     * @param type
     * @param execute
     * @param field
     * @return
     */
    private static JobHandleParamInfo executeReturn(Type type, Execute execute, Field field) {

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
        if (clazz == null || clazz == void.class || clazz == Void.class) {
            return null;
        } else if (clazz.isArray()) {
            jobHandleParamInfo.setName("Array");
            jobHandleParamInfo.setClassName("Array");

            Class<?> newClass = clazz.getComponentType();
            jobHandleParamInfo.addChildren(executeReturn(newClass, null, null));
        } else if (ReflectionUtil.isPrimitive(clazz)) {
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
        } else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                for (Type newType : types) {
                    jobHandleParamInfo.addChildren(executeReturn(newType, null, null));
                }
            }
        } else {
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Method> mapMethods = ReflectionUtil.getBeanPropertyReadMethods(clazz);
            for (Field f : fields) {
                if (!mapMethods.containsKey(f.getName())) {
                    continue;
                }
                Class<?> fieldClass = f.getType();
                Type genericType = f.getGenericType();
                if (genericType.getTypeName().equals("T") && type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    fieldClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
                jobHandleParamInfo.addChildren(executeReturn(fieldClass, null, f));
            }
        }
        return jobHandleParamInfo;
    }


    // ---------------------- job thread repository ----------------------
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();

    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);    // putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static void removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }

    public static JobThread loadJobThread(int jobId) {
        JobThread jobThread = jobThreadRepository.get(jobId);
        return jobThread;
    }

    public static Integer[] getJobTreadSize(){
        Integer[] array = new Integer[jobThreadRepository.size()];
        return  jobThreadRepository.keySet().toArray(array);
    }
}
