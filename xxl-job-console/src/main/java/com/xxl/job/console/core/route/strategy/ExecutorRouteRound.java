package com.xxl.job.console.core.route.strategy;

import com.xxl.job.console.core.route.ExecutorRouter;
import com.xxl.job.console.model.App;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteRound extends ExecutorRouter {

    private static ConcurrentMap<Long, Integer> routeCountEachJob = new ConcurrentHashMap<Long, Integer>();
    private static long CACHE_VALID_TIME = 0;
    private static int count(long jobId) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        // count++
        Integer count = routeCountEachJob.get(jobId);
        count = (count==null || count>1000000)?(new Random().nextInt(100)):++count;  // 初始化时主动Random一次，缓解首次压力
        routeCountEachJob.put(jobId, count);
        return count;
    }

    @Override
    public ReturnT<App> route(TriggerParam triggerParam, List<App> apps) {
        App app = apps.get(count(triggerParam.getJobId())%apps.size());
        return new ReturnT<App>(app);
    }

}
