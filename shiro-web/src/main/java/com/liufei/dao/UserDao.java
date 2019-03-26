package com.liufei.dao;


import com.liufei.vo.User;

import java.util.List;

public interface UserDao {

    /**
     *  查询是否有该用户
     *  userName：用户名
     */
    User getUserByUserName(String userName);

    /**
     * 根据用户名查询该用户的角色
     * @param userName
     * @return
     */
    List<String> queryRolesByUserName(String userName);
}
