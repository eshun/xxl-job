package com.xxl.job.console.service.impl;

import com.xxl.job.console.model.*;
import com.xxl.job.console.core.thread.JobTriggerPoolHelper;
import com.xxl.job.console.core.trigger.TriggerTypeEnum;
import com.xxl.job.console.core.util.I18nUtil;
import com.xxl.job.console.dao.JobInfoDao;
import com.xxl.job.console.dao.JobLogDao;
import com.xxl.job.console.service.ActuatorService;
import com.xxl.job.console.service.AppService;
import com.xxl.job.core.biz.ConsoleBiz;
import com.xxl.job.core.biz.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class ConsoleBizImpl implements ConsoleBiz {
    private static Logger logger = LoggerFactory.getLogger(ConsoleBizImpl.class);

    private List<ActuatorParam> actuatorParams;

    @Resource
    public JobLogDao jobLogDao;
    @Resource
    private JobInfoDao jobInfoDao;

    @Resource
    AppService appService;
    @Resource
    ActuatorService actuatorService;

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==ReturnT.SUCCESS_CODE?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = jobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        ReturnT<Object> callbackResult=(ReturnT<Object>)handleCallbackParam.getExecuteResult();
        // trigger success, to trigger child job
        String callbackMsg = null;
        if (ReturnT.FAIL_CODE == callbackResult.getCode()) {
            JobInfo jobInfo = jobInfoDao.loadById(log.getJobId());
            if (jobInfo !=null && jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                String[] childJobIds = jobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (childJobIds[i]!=null && childJobIds[i].trim().length()>0 && isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg()!=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (callbackResult.getMsg() != null) {
            handleMsg.append(callbackResult.getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(callbackResult.getCode());
        log.setHandleMsg(handleMsg.toString());
        jobLogDao.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    private boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        if (registryParam != null) {
            String name = registryParam.getRegistName();
            String ip = registryParam.getRegistryIp();
            int port = Integer.parseInt(registryParam.getRegistryPort());

            App app = appService.loadBy(name, ip, port);
            if (app != null) {
                app.setOnline(0);
                app.setUpdateTime(new Date());
                app.setJobInfo(getJobInfo(registryParam));
                appService.update(app);
            } else {
                app = new App();
                app.setOnline(0);
                app.setIp(ip);
                app.setName(name);
                app.setPort(port);
                app.setJobInfo(getJobInfo(registryParam));
                appService.insert(app);
            }
            actuatorParams=new ArrayList<>();
            initJobHandle(app, registryParam);
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        if (registryParam != null) {
            String name = registryParam.getRegistName();
            String ip = registryParam.getRegistryIp();
            int port = Integer.parseInt(registryParam.getRegistryPort());
            App app = appService.loadBy(name, ip, port);
            if (app != null) {
                app.setOnline(1);
                app.setUpdateTime(new Date());
                app.setJobInfo("");
                appService.update(app);
            }
        }
        return ReturnT.SUCCESS;
    }

    private String getJobInfo(RegistryParam registryParam){
        Integer[] jobs = registryParam.getJobs();
        String jobInfoStr = "";
        if (jobs != null && jobs.length > 0) {
            for (Integer job : jobs) {
                jobInfoStr += job + ",";
            }
            jobInfoStr = jobInfoStr.substring(0, jobInfoStr.length() - 1);
        }
        return jobInfoStr;
    }

    private void initJobHandle(App app,RegistryParam registryParam) {
        List<JobHandleInfo> jobHandleInfos = registryParam.getJobHandleInfos();

        if (jobHandleInfos != null && !jobHandleInfos.isEmpty()) {
            for (JobHandleInfo jobHandleInfo : jobHandleInfos) {
                Actuator actuator = actuatorService.loadByNameAndApp(jobHandleInfo.getName(), app.getId());
                if (actuator == null) {
                    actuator = new Actuator();
                    actuator.setName(jobHandleInfo.getName());
                    actuator.setMethod(jobHandleInfo.getMethodName());
                    actuator.setSerialVersionUid(jobHandleInfo.getSerialVersionUID());
                    actuator.setReturnExample(jobHandleInfo.getReturnExample());
                    actuator.setParamMd5("");

                    initParam(jobHandleInfo.getGenericReturn(),1,actuator.getId(),null);
                    for (JobHandleParamInfo info : jobHandleInfo.getGenericParameters()) {
                        initParam(info,0,actuator.getId(),null);
                    }
                    actuator.setActuatorParams(actuatorParams);

                    actuatorService.insert(actuator, app.getId());

                } else {
                    boolean isChange = jobHandleInfo.isCover() || !actuator.getSerialVersionUid().equals(jobHandleInfo.getSerialVersionUID());
                    if (isChange) {
                        actuator.setMethod(jobHandleInfo.getMethodName());
                        actuator.setSerialVersionUid(jobHandleInfo.getSerialVersionUID());
                        actuatorService.deleteParam(actuator.getId());

                        initParam(jobHandleInfo.getGenericReturn(),1,actuator.getId(),null);
                        for (JobHandleParamInfo info : jobHandleInfo.getGenericParameters()) {
                            initParam(info,0,actuator.getId(),null);
                        }
                        actuator.setActuatorParams(actuatorParams);
                    }
                    actuator.setStatus(0);
                    actuator.setUpdateTime(new Date());
                    actuatorService.update(actuator);

                }
            }
        }
    }

    private void initParam(JobHandleParamInfo info,Integer paramType,Long actuatorId,Long pid) {
        ActuatorParam actuatorParam = new ActuatorParam();
        actuatorParam.setActuatorId(actuatorId);
        actuatorParam.setParentId(pid);
        actuatorParam.setParamType(paramType);
        actuatorParam.setClassName(info.getClassName());
        actuatorParam.setDefaultValue(info.getDefaultValue());
        actuatorParam.setName(info.getName());
        actuatorParam.setValue(info.getValue());
        actuatorParam.setParamOrder(info.getParamOrder());
        actuatorParam.setRequired(info.isRequired());
        actuatorParams.add(actuatorParam);
        if (info.getChildren() != null && !info.getChildren().isEmpty()) {
            for (JobHandleParamInfo child : info.getChildren()) {
                initParam(child, paramType, actuatorId, actuatorParam.getId());
            }
        }
    }
}
