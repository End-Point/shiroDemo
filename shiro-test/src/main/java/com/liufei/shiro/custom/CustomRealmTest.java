package com.liufei.shiro.custom;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * 步骤五：。。。。(步骤四为CustomRealm，步骤六为ShiroEncrypt)
 * 测试自定义realm(CustomRealm)，
 */
public class CustomRealmTest {

    @Test
    public void authentication(){

        //创建自定义realm对象
         CustomRealm customRealm = new CustomRealm();


    //第一步：因为所有的操作都是在SecurityManager中执行的，先创建该对象默认的SecurityManager，设置shiro环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        //将自定义Realm添加到DefaultSecurityManager中
        defaultSecurityManager.setRealm(customRealm);



    //第二步：主体(用户)提交认证请求(Subject对象)，使用shiro中的SecurityUtils类getSubject()方法可以获取到用户提交过来的请求,这里要引入shiro的包。然后进行认证，就是数据库中的
        //数据做对比，相同则认证成功，否则失败
        //1.设置环境，指定在哪个SecurityManager中运行
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //2. 创建Subject对象，调用 SecurityUtils类中的getSubject获取用户的请求，该功能主要是获取主体(用户)请求的
        Subject subject = SecurityUtils.getSubject();
        // 3.认证并提交登录信息，UsernamePasswordToken用来存储主体传过来的数据，一般是用户名和密码。
        UsernamePasswordToken token = new UsernamePasswordToken("xiaoming","123456");
        //Subject提交登录信息
        subject.login(token);
        //subject.isAuthenticated() 验证是否登录成功。
        System.out.println("是否认证成功："+subject.isAuthenticated());

        //验证权限
        subject.checkRoles("admin");
        subject.checkPermissions("user:update","user:delect");



    }
}
