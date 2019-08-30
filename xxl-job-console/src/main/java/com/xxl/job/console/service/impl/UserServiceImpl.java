package com.xxl.job.console.service.impl;

import com.xxl.job.console.dao.UserDao;
import com.xxl.job.console.model.User;
import com.xxl.job.console.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-08-30
 */
@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            return userDao.loadByUserName(userName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new UsernameNotFoundException("errorMsg");
        }
    }

    @Override
    public List<User> query(String userName, int offset, int pageSize) {
        try {
            return userDao.query(userName, offset, pageSize);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int total(String userName) {
        try {
            return userDao.total(userName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int insert(User user) {
        try {
            return userDao.insert(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int update(User user) {
        try {
            return userDao.update(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int delete(Long id) {
        try {
            return userDao.delete(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public User loadById(Long id) {
        try {
            return userDao.loadById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> queryByUserName(String userName, int userRole) {
        try {
            return userDao.queryByUserName(userName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
