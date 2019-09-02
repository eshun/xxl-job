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
public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public ReturnT<App> route(TriggerParam triggerParam, List<App> apps) {

        StringBuffer beatResultSB = new StringBuffer();
        for (App app : apps) {
            // beat
            String address=app.getIp() + ":" + app.getPort();
            ReturnT<String> beatResult = null;
            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
            }
            beatResultSB.append( (beatResultSB.length()>0)?"<br><br>":"")
                    .append(I18nUtil.getString("jobconf_beat") + "：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
                ReturnT<App> result=new ReturnT<App>(app);
                result.setMsg(beatResultSB.toString());
                return result;
            }
        }
        return new ReturnT<App>(ReturnT.FAIL_CODE, beatResultSB.toString());

    }
}
