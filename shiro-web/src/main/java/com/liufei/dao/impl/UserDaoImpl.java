package com.liufei.dao.impl;

import com.liufei.dao.UserDao;
import com.liufei.vo.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserDaoImpl implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    public User getUserByUserName(String userName) {
        String sql = "select * from user where user_name = ?";
        List<User> list = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUserName(resultSet.getString("user_name"));
                user.setPassWord(resultSet.getString("password"));
                return user;
            }
        });
        //如果集合时空，返回null
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        return list.get(0);
    }

    /**
     * 根据用户名查询角色信息
     * @param userName
     * @return
     */
    public List<String> queryRolesByUserName(String userName) {
        String sql = "SELECT * FROM `user_roles` WHERE username = ?";
        List<String> listRoles = jdbcTemplate.query(sql,new String[]{userName},new RowMapper<String>(){

            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("role_name");
            }
        });

        //如果集合时空，返回null
        if(CollectionUtils.isEmpty(listRoles)){
            return null;
        }
        return listRoles;

    }
}
