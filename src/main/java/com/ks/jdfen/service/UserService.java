package com.ks.jdfen.service;

import com.ks.jdfen.Entity.User;

/**
 * @author ：imc
 * @date ：Created in 2020/3/23 3:07 下午
 * @description：
 */
public interface UserService {
    User findUserByName(String username);

    void saveUser(User user);
}
