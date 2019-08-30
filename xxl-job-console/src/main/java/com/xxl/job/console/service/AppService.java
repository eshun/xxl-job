package com.xxl.job.console.service;

import com.xxl.job.console.model.App;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
public interface AppService {

    /**
     * 新增
     * @param app
     * @return
     */
    int insert(App app);

    /**
     * 刪除
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 更新
     * @param app
     * @return
     */
    int update(App app);

    /**
     * 超时下线
     * @param timeout
     * @return
     */
    int offline(int timeout);

    /**
     * 获取一条数据
     * @param id
     * @return
     */
    App load(Long id);

    /**
     * 根据运用名 ip port获取
     * @param name
     * @param ip
     * @param port
     * @return
     */
    App loadBy(String name,String ip,Integer port);

    /**
     * 分页查询
     * @param offset
     * @param pageSize
     * @return
     */
    List<App> query(int offset, int pageSize);

    /**
     * 查询条数
     * @param offset
     * @param pageSize
     * @return
     */
    int count(int offset, int pageSize);
}
