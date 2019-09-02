package com.xxl.job.console.core.route.strategy;

import com.xxl.job.console.config.XxlJobScheduler;
import com.xxl.job.console.core.route.ExecutorRouter;
import com.xxl.job.console.core.util.I18nUtil;
import com.xxl.job.console.model.App;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteBusyover extends ExecutorRouter {

    @Override
    public ReturnT<App> route(TriggerParam triggerParam, List<App> apps) {
        StringBuffer idleBeatResultSB = new StringBuffer();
        for (App app : apps) {
            // beat
            String address=app.getIp() + ":" + app.getPort();
            ReturnT<String> idleBeatResult = null;
            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
                idleBeatResult = executorBiz.idleBeat(triggerParam.getJobId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                idleBeatResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
            }
            idleBeatResultSB.append( (idleBeatResultSB.length()>0)?"<br><br>":"")
                    .append(I18nUtil.getString("jobconf_idleBeat") + "：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(idleBeatResult.getCode())
                    .append("<br>msg：").append(idleBeatResult.getMsg());

            // beat success
            if (idleBeatResult.getCode() == ReturnT.SUCCESS_CODE) {
                ReturnT<App> result=new ReturnT<App>(app);
                result.setMsg(idleBeatResultSB.toString());
                return result;
            }
        }

        return new ReturnT<App>(ReturnT.FAIL_CODE, idleBeatResultSB.toString());
    }

}
