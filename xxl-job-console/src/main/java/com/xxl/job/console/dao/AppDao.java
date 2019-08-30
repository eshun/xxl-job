package com.xxl.job.console.dao;

import com.xxl.job.console.model.JobApp;
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
    int insert(JobApp jobApp);

    /**
     * [刪除]
     * @author esun
     * @date 2019/08/26
     **/
    int delete(@Param("id") int id);

    /**
     * [更新]
     * @author esun
     * @date 2019/08/26
     **/
    int update(JobApp jobApp);

    /**
     * [查詢] 根據主鍵 id 查詢
     * @author esun
     * @date 2019/08/26
     **/
    JobApp load(@Param("id") int id);

    /**
     * [查詢] 分頁查詢
     * @author esun
     * @date 2019/08/26
     **/
    List<JobApp> query(@Param("offset") int offset,
                       @Param("pageSize") int pageSize);

    /**
     * [查詢] 分頁查詢 count
     * @author esun
     * @date 2019/08/26
     **/
    int count(@Param("offset") int offset,
                      @Param("pageSize") int pageSize);
}
