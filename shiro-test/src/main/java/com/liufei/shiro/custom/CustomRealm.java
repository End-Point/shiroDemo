package com.liufei.shiro.custom;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.*;

/**
 * 步骤四：。。。。(步骤三为JdbcRealmTest，步骤五为CustomRealmTest)
 * shiro中自定义realm,该类和它的的test类集成姐加密工具 加密工具地址：com.liufei.shiro.encrypt.ShiroEncrypt
 *  1.继承AuthorizingRealm抽象类
 *  这里是使用集合模拟从数据库或者缓存中取数据
 */
public class CustomRealm extends AuthorizingRealm {

    Map<String,String> dataBaseUser = new HashMap<String, String>();//存放用户信息
    Set<String> dataBaseRole = new HashSet<String>(); //根据用户获取角色信息,使用的方法需要传入set集合
    Set<String> dataBasePermission = new HashSet<String>();  //根据角色获取权限信息


    {
        //初始化集合，并存入数据
        //注意：这个集成了加密算法，验证时需要将密码改为密文才能验证成功  这里明文为123456
        //加盐后的密文，salt值为“LPF”
        dataBaseUser.put("xiaoming","24542476c8c3a537c94935bf36c786be");

        //存入角色信息
        dataBaseRole.add("admin");

        //存入角色权限信息
        dataBasePermission.add("user:update");
        dataBasePermission.add("user:delect");

        //设置自定义realm的名称，在下边使用
        super.setName("customRealm");

    }

    /**
     * 用来做授权的方法,从数据库中获取角色和权限信息
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        //1.获取主体的唯一id
        String userName = (String) principalCollection.getPrimaryPrincipal();

        //从数据库或者缓存中获取角色信息
        Set<String> role = this.selectRole(userName);

        //通过角色获取权限信息
        String aa= null;
        Set<String> permission = this.selectPermission(aa);

        //通过角色获取权限信息

        /*
           该类中有两个设置角色权限的方法：(1)setStringPermissions:传入字符串类型的权限信息
            (2)setObjectPermissions:传入set集合类型的权限信息
           有一个设置角色的方法：
             (1)setRoles: 设置该主体拥有的角色
         */
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
           authorizationInfo.setRoles(role);
           authorizationInfo.setStringPermissions(permission);

           return authorizationInfo;
    }


    /**
     * 用来做认证的方法，从数据库中获取主体的信息
     * @param authenticationToken：接受主体(用户)传过来的认证信息
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //authenticationToken中保存着主体的唯一id，可以通过唯一id去数据库查询是否有该主体，这就是认证的过程

        //1.获取主体的唯一id信息，一般是用户名
        String userName = (String) authenticationToken.getPrincipal();

        //从数据库或者缓存中获取主体(用户)信息
        String passWord = this.selectUser(userName);

        if(passWord.equals("")){
            return null;
        }
        //如果数据库存在该主体信息，则继续操作
        //创建AuthenricationInfo 并通过它的构造方法传入用户名/密码和realm的名字  这三个参数
        AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,passWord,"customRealm");
        //加盐这里集成了ShiroEncrypt加密类。。加盐的方法是SimpleAuthenticationInfo独有的，所以要强转。
        //ByteSource.Util.bytes()方法是将salt值加入转成其他进制的方法
        ((SimpleAuthenticationInfo) authenticationInfo).setCredentialsSalt(ByteSource.Util.bytes("LPF"));

        return authenticationInfo;
    }

    /**
     * 通过唯一id，取数据查询，是否存在这个主体，这里使用map集合存储数据模拟数据库
     * @param userName
     * @return
     */
    private String selectUser(String userName){
       return dataBaseUser.get(userName);
    }

    /**
     * 根据用户名获取角色
     * @param userName
     * @return
     */
    private Set<String> selectRole(String userName){
        return dataBaseRole;
    }

    /**
     * 通过角色获取权限信息
     * @param roleName
     * @return
     */
    private Set<String> selectPermission(String roleName){
        return dataBasePermission;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456","LPF");
        System.out.println(md5Hash.toString());
    }
}
