package shiroTest;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import javax.sql.DataSource;

/**
 *步骤三：。。。(步骤二为JdbcRealmTest，步骤四为CustomRealm)
 * shiro两个内置Realm：IniRealm使用和JdbcRealm
 * JdbcRealm使用：需要创建数据库表，这里没有 创建，会报错。
 */
public class JdbcRealmTest {
    //配置数据源信息
   DruidDataSource druidDataSource2 = new DruidDataSource();
    {
        druidDataSource2.setUrl("jdbc:mysql://localhost:3306/shiro_test");
        druidDataSource2.setUsername("root");
        druidDataSource2.setPassword("123456");
    }


    @Test
    public void authentication(){

        //创建JdbcRealm对象，并把配置好的数据源放入JdbcRealm中,JdbcRealm内置了默认的sql语句，所以在某些情况下，不需要xiesql语句就可以查询，
        //点击JdbcRealm类会看到默认的sql语句
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(druidDataSource2);
        //JdbcRealm默认不会查询权限，这里需要设置为true，开启权限查询。
        jdbcRealm.setPermissionsLookupEnabled(true);

        //自定义sql
        String sql = "select user_name password from users2 where user_name = ?";
        jdbcRealm.setAuthenticationQuery(sql);


    //第一步：因为所有的操作都是在SecurityManager中执行的，先创建该对象默认的SecurityManager，设置shiro环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        //将JdbcRealm添加到DefaultSecurityManager中
        defaultSecurityManager.setRealm(jdbcRealm);



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
        subject.checkRoles("admin");



    }
}
