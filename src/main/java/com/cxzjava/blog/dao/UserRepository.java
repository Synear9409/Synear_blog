package com.cxzjava.blog.dao;

import com.cxzjava.blog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    ///根据用户名和密码查找用户的JPA封装好的方法
    User findByUsernameAndPassword(String username, String password);
}