package com.xxl.job.console.service.impl;

import com.xxl.job.console.model.App;
import com.xxl.job.console.model.XxlJobInfo;
import com.xxl.job.console.model.XxlJobLog;
import com.xxl.job.console.core.thread.JobTriggerPoolHelper;
import com.xxl.job.console.core.trigger.TriggerTypeEnum;
import com.xxl.job.console.core.util.I18nUtil;
import com.xxl.job.console.dao.XxlJobInfoDao;
import com.xxl.job.console.dao.XxlJobLogDao;
import com.xxl.job.console.service.AppService;
import com.xxl.job.core.biz.ConsoleBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class ConsoleBizImpl implements ConsoleBiz {
    private static Logger logger = LoggerFactory.getLogger(ConsoleBizImpl.class);

    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Resource
    AppService appService;

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
        XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
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
            XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
            if (xxlJobInfo!=null && xxlJobInfo.getChildJobId()!=null && xxlJobInfo.getChildJobId().trim().length()>0) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
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
        xxlJobLogDao.updateHandleInfo(log);

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
            Integer[] jobs = registryParam.getJobs();

            App app = appService.loadBy(name, ip, port);
            if (app != null) {
                app.setOnline(0);
                app.setUpdateTime(new Date());
                if (jobs != null && jobs.length > 0) {
                    String jobInfoStr = null;
                    for (Integer job : jobs) {
                        jobInfoStr += job + ",";
                    }
                    jobInfoStr = jobInfoStr.substring(0, jobInfoStr.length() - 1);
                    app.setJobInfo(jobInfoStr);
                }
                appService.update(app);
            } else {
                app = new App();
                app.setOnline(0);
                app.setIp(ip);
                app.setName(name);
                app.setPort(port);
                appService.insert(app);
            }
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

    private void initExecutor(){

    }
}
