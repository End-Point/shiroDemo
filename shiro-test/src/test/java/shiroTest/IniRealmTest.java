package shiroTest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * 步骤二：。。。(步骤一为shiroAuthenticatorAndAuthorizer类，步骤三为：JdbcRealmTest)
 * shiro两个内置Realm：IntRealm使用和JdbcRealm
 * IntRealm使用
 */
public class IniRealmTest {

    @Test
    public void authentication(){

        //设置文件路径,在认证时，authenticator调用realm去这个路径下找用户名和密码进行认证。
        /*
          user.ini文件格式：
            [users]【说明以下信息为用户信息】
            LPF=123456,admin 【用户名=密码，角色】 意思为用户名时LPF，密码为123456，角色时管理员
            [rooles] 【说明以下信息时角色的权限信息】
            admin=user:delete,user:update【角色=信息:操作,信息，操作】 意思为admin角色拥有删除用户信息和修改用户信息权限
         */
        IniRealm iniRealm = new IniRealm("classpath:user.ini");

        //第一步：因为所有的操作都是在SecurityManager中执行的，先创建该对象默认的SecurityManager，设置shiro环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();

          //将IniRealm添加到环境中。
          defaultSecurityManager.setRealm(iniRealm);


    //第二步：主体(用户)提交认证请求(Subject对象)，使用shiro中的SecurityUtils类getSubject()方法可以获取到用户提交过来的请求,这里要引入shiro的包。然后进行认证，就是数据库中的
        //数据做对比，相同则认证成功，否则失败
        //1.设置环境，指定在哪个SecurityManager中运行
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //2. 创建Subject对象，调用 SecurityUtils类中的getSubject获取用户的请求，该功能主要是获取主体(用户)请求的
        Subject subject = SecurityUtils.getSubject();
        // 3.认证并提交登录信息，UsernamePasswordToken用来存储主体传过来的数据，一般是用户名和密码。
        UsernamePasswordToken token = new UsernamePasswordToken("LPF","123456");
        //Subject提交登录信息
        subject.login(token);
        //subject.isAuthenticated() 验证是否登录成功。
        System.out.println("是否认证成功："+subject.isAuthenticated());

    //第三步：授权，还是使用subject类，获取角色和权限数据，给主体授权
        //1.首先要确认(认证)主体是什么角色，应该赋予什么权限，所有的角色和权限都是从数据库或者缓存中获取的
        subject.checkRole("admin"); //单个角色认证：认证主体是否是admin角色
        subject.checkRoles("admin");//多个角色认证，认证主体是否admin和用户两个角色

        //2.认证完角色后，继续认证该角色有什么权限
        subject.checkPermission("user:delete"); //验证单个权限，认证admin角色是否有删除用户的权限
        subject.checkPermissions("user:delete","user:update"); //认证多个权限，认证admin角色是否有删除用户和修改用户信息的权限，这里可以传入Collection类型的集合



    }
}
