package com.ks.jdfen.dao;

import com.ks.jdfen.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<User, Long> {
    @Query(value = "select * from user_t where username = ?", nativeQuery = true)
    User findByName(String name);

//    @Query
//    @Modifying("update MoneyPO m set m.isDeleted=?2 where  m.money=?1")
//    void settleAccount(String username, int total);
}
