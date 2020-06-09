package com.cxzjava.blog.service;

import com.cxzjava.blog.pojo.User;

public interface UserService {
    User checkUser(String username,String password);
}
