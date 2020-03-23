package com.ks.jdfen.service.impl;

import com.ks.jdfen.Entity.User;
import com.ks.jdfen.controller.dao.UserDao;
import com.ks.jdfen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：imc
 * @date ：Created in 2020/3/23 3:08 下午
 * @description：
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public User findUserByName(String username) {
        return userDao.findByName(username);
    }

    @Override
    public void saveUser(User user) {
        userDao.save(user);
    }
}
