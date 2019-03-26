package shiroTest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

import java.security.PublicKey;

/**
 * 此步骤按照class划分，步骤一。。(步骤2时：IniRealmTest类)
 * shiro框架：
 *  简单模拟认证(登录)和部分授权的流程
 */
public class shiroAuthenticatorAndAuthorizer {

    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
    /**
     * 模拟数据库信息，如果UsernamePasswordToken的信息和这里信息不符，则认证失败。
     */
    @Before
    public void addUser(){
        //添加用户名、密码和角色。ddAccount("userName"【账户】,"passWord"【密码】,"admin"【角色，可变参数】);
        simpleAccountRealm.addAccount("userName","passWord","admin");
    }

    @Test
    public void authentication(){

        //第一步：因为所有的操作都是在SecurityManager中执行的，先创建该对象默认的SecurityManager，设置shiro环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();

          //将simpleAccountRealm(包含主体的用户名和密码)设置到SecurityManager环境中
          defaultSecurityManager.setRealm(simpleAccountRealm);

        //第二步：主体(用户)提交认证请求(Subject对象)，使用shiro中的SecurityUtils类getSubject()方法可以获取到用户提交过来的请求,这里要引入shiro的包。然后进行认证，就是数据库中的
        //数据做对比，相同则认证成功，否则失败
            //1.设置环境，指定在哪个SecurityManager中运行
            SecurityUtils.setSecurityManager(defaultSecurityManager);
            //2. 创建Subject对象，调用 SecurityUtils类中的getSubject获取用户的请求，该功能主要是获取主体(用户)请求的
            Subject subject = SecurityUtils.getSubject();
            // 3.认证并提交登录信息，UsernamePasswordToken用来存储主体传过来的数据，一般是用户名和密码。
            UsernamePasswordToken token = new UsernamePasswordToken("userName","passWord");
            //Subject提交登录信息
            subject.login(token);
            //subject.isAuthenticated() 验证是否登录成功。
            System.out.println("是否认证成功："+subject.isAuthenticated());

        //第三步：授权，还是使用subject类，获取角色和权限数据，给主体授权
           //1.首先要确认(认证)主体是什么角色，应该赋予什么权限，所有的角色和权限都是从数据库或者缓存中获取的
           subject.checkRole("admin"); //单个角色认证：认证主体是否是admin角色
           subject.checkRoles("admin","user");//多个角色认证，认证主体是否admin和用户两个角色

           //因为无法模拟授权，需要在记录 realms时，添加授权的笔记
            //退出方法
            subject.logout();

    }
}
