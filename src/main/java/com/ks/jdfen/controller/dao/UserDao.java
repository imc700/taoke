package com.ks.jdfen.controller.dao;

import com.ks.jdfen.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<User, Long> {
    @Query(value = "select * from user_t where username = ?", nativeQuery = true)
    User findByName(String name);
}
