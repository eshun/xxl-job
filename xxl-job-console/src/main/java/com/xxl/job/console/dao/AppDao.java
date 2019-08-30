package com.xxl.job.console.dao;

import com.xxl.job.console.model.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author esun
 * @version v1.0
 * @date: 2019/8/26
 */
@Mapper
public interface AppDao {

    /**
     * [新增]
     * @author esun
     * @date 2019/08/26
     **/
    int insert(App app);

    /**
     * [刪除]
     * @author esun
     * @date 2019/08/26
     **/
    int delete(@Param("id") Long id);

    /**
     * [更新]
     * @author esun
     * @date 2019/08/26
     **/
    int update(App app);

    /**
     * 超时下线
     * @param timeout
     * @return
     */
    int offline(@Param("timeout") int timeout);

    /**
     * [查詢] 根據主鍵 id 查詢
     * @author esun
     * @date 2019/08/26
     **/
    App load(@Param("id") Long id);

    /**
     * 根据运用名 ip port获取
     * @param name
     * @param ip
     * @param port
     * @return
     */
    App loadBy(@Param("name") String name,@Param("ip") String ip,@Param("port") Integer port);

    /**
     * [查詢] 分頁查詢
     * @author esun
     * @date 2019/08/26
     **/
    List<App> query(@Param("offset") int offset,
                    @Param("pageSize") int pageSize);

    /**
     * [查詢] 分頁查詢 count
     * @author esun
     * @date 2019/08/26
     **/
    int count(@Param("offset") int offset,
                      @Param("pageSize") int pageSize);
}
