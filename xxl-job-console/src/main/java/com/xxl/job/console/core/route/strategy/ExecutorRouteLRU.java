package com.xxl.job.console.core.route.strategy;

import com.xxl.job.console.core.route.ExecutorRouter;
import com.xxl.job.console.model.App;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 单个JOB对应的每个执行器，最久为使用的优先被选举
 *      a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 *      b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 *
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLRU extends ExecutorRouter {

    private static ConcurrentMap<Long, LinkedHashMap<Long, App>> jobLRUMap = new ConcurrentHashMap<Long, LinkedHashMap<Long, App>>();
    private static long CACHE_VALID_TIME = 0;

    public App route(long jobId, List<App> apps) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        // init lru
        LinkedHashMap<Long, App> lruItem = jobLRUMap.get(jobId);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）；false=插入顺序排期；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<Long, App>(16, 0.75f, true);
            jobLRUMap.putIfAbsent(jobId, lruItem);
        }

        // put new
        for(App app: apps){
            if (!lruItem.containsKey(app.getId())) {
                lruItem.put(app.getId(), app);
            }
        }
        // remove old
        List<App> delKeys = new ArrayList<>();
        for (Long existKey: lruItem.keySet()) {
            for(App app: apps){
                if (!app.getId().equals(existKey)) {
                    delKeys.add(app);
                }
            }
        }
        if (delKeys.size() > 0) {
            for (App delKey: delKeys) {
                lruItem.remove(delKey);
            }
        }

        // load
        long eldestKey = lruItem.entrySet().iterator().next().getKey();
        App eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }

    @Override
    public ReturnT<App> route(TriggerParam triggerParam, List<App> apps) {
        App app = route(triggerParam.getJobId(), apps);
        return new ReturnT<App>(app);
    }

}
