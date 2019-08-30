package com.xxl.job.console.service;

import com.xxl.job.console.model.User;

import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
public interface UserService {
    
    /**
     * 根据 userName 查詢
     * @param userName
     * @return
     */
    public User loadUserByUsername(String userName);

    /**
     * 分页查询
     *
     * @param userName LIKE "%userName%"
     * @param offset
     * @param pageSize
     * @return
     */
    List<User> query(String userName, int offset, int pageSize);

    /**
     * 分页查询总条数 count
     *
     * @param userName LIKE "%userName%"
     * @return
     */
    int total(String userName);

    /**
     * 新增
     *
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 更新
     *
     * @param user
     * @return
     */
    int update(User user);

    /**
     * 刪除
     *
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 根据 id 查詢
     *
     * @param id
     * @return
     */
    User loadById(Long id);

    /**
     * 根据 userName 获取用户
     * @param userName like
     * @param userRole
     * @return
     */
    List<User> queryByUserName(String userName,int userRole);
}
