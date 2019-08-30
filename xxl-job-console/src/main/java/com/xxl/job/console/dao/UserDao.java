package com.xxl.job.console.dao;

import com.xxl.job.console.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
@Mapper
public interface UserDao {

    /**
     * 根据 userName 查詢
     * @param userName
     * @return
     */
    User loadByUserName(@Param("userName") String userName);

    /**
     * 分页查询
     * @param userName LIKE "%userName%"
     * @param offset
     * @param pageSize
     * @return
     */
    List<User> query(@Param("userName") String userName, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 分页查询总条数 count
     * @param userName LIKE "%userName%"
     * @return
     */
    int total(@Param("userName") String userName);

    /**
     * 新增
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 更新
     * @param user
     * @return
     */
    int update(User user);

    /**
     * 刪除
     * @param id
     * @return
     */
    int delete(@Param("id") Long id);

    /**
     * 根据 id 查詢
     * @param id
     * @return
     */
    User loadById(@Param("id") Long id);

    /**
     * 根据 userName 获取用户
     * @param userName like
     * @return
     */
    List<User> queryByUserName(@Param("userName") String userName);
}
