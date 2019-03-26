package com.liufei.shiro.custom.encrypt;

import com.liufei.shiro.custom.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * 步骤六：。。。。(步骤五为CustomRealmTest)
 * shiro加密和自定义Realm进行了集成，因为Realm是操作数据库的工具，经常和数据打交道，所以需要加密
 * 在此类中设置加密，在realm中设置加盐操作(如果不需要加盐则不设置)。
 */
public class ShiroEncrypt {

    @Test
    public void authentication(){

        //创建自定义realm对象
        CustomRealm customRealm = new CustomRealm();

     //第一步：因为所有的操作都是在SecurityManager中执行的，先创建该对象默认的SecurityManager，设置shiro环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        //1. 将自定义Realm添加到DefaultSecurityManager中
        defaultSecurityManager.setRealm(customRealm);
        //2.使用shiro的加密工具
        HashedCredentialsMatcher hashedCredentials = new HashedCredentialsMatcher();
            //选择加密方法,填写加密工具的名称即可
            hashedCredentials.setHashAlgorithmName("MD5");
            //设置加密次数，可以为1此或者多次加密，注意，如果加密多次，在解密时也要解密相同的次数
            hashedCredentials.setHashIterations(1);
            //因为是从数据取出数据或者将数据存入数据，操作数据库需要使用realm，所以将设置好的加密工具放入realm中
            customRealm.setCredentialsMatcher(hashedCredentials);


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
