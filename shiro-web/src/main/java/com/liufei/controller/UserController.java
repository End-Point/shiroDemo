package com.liufei.controller;


import com.liufei.vo.User;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.*;

@Controller
@RequestMapping("/userCollection")
public class UserController {

    @RequestMapping(value = "/subLogin.do",method = RequestMethod.POST)
    @ResponseBody
    public String subLogin(User user){
        //获取主体信息
        Subject subject = SecurityUtils.getSubject();
        //提交主体请求信息
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(),user.getPassWord());
        subject.login(token);
        System.out.println("登录成功，所在位置："+UserController.class);

        //shiro授权有两种方式，1.通过编程方式进行授权操作 2.通过注解方式进行授权操作。这是使用的时编程方式。
         //TODO！！！注意：在使用自定义缓存时，这里会报错。
//        if(subject.hasRole("admin")){
//
//            return "有admin权限，登录成功";
//        }
        return "没有admin权限，登录成功";

    }
  /////////////////////////////////////////////shiro内置的权限验证 start//////////
    /**
     *  shiro内置的权限验证
     *
     *  属性说明：logical：属性位于)@RequiresRoles注解、和@RequiresPermissions注解他又两种格式
     *   ①	logical = Logical.AND：表示请求主体必须同时具有多个角色或权限才可以访问该接口。
     *   ②	logical = Logical.OR:表示请求主体拥有多个角色或权限的其中一个就可以访问该接口<br>
     *  <li>五种权限验证注解：
     * (1)@RequiresUser:被定义为一个拥有已知身份，或在当前session 中由于通过验证被确认，或者在之前session 中的'RememberMe'服务被记住。
     * (2)@RequiresRoles(value = {"admin","admin1"},logical = Logical.AND):
     *   表示当前请求主体必须同时具有 admin和admin1两个角色才可以访问这个接口。如果是logical = Logical.OR 就是或者的意思。
     *    要求当前的Subject 拥有所有指定的角色。如果他们没有，则该方法将不会被执行，而且AuthorizationException 异常将会被抛出。
     *    当前请求的主体必须具备admin角色才可以访问该接口。<br>
     *(3)@RequiresPermissions({"user:update",”user:delete”}, logical = Logical.OR):
     *   表示当前请求主体拥有“user:update”和“user:delete”其中一个权限就可以访问该接口。
     *    要求当前请求主体必须符合当前权限才可以访问该接口。(推荐使用)<br>
     *(4)@RequiresAuthentication:表明当前Subject(请求主体) 已经在当前的session 中被验证通过才能被访问或调用该接口。<br>
     *(5)@RequiresGuest:要求当前的Subject 是一个"guest"，也就是说，他们必须是在之前的session 中没有被验证或被记住才能被访问或调用。<br>
     * </li>
     * @return
     */
//    @RequiresRoles(value = {"admin","admin1"},logical = Logical.AND) //表示当前请求的主体必须具备admin角色才可以访问该接口,否则会报错
//    @RequiresPermissions("user:update")
    @RequestMapping(value = "testRole",method =RequestMethod.GET)
    @ResponseBody
    public String testRoles(){
        return "testRole success";
    }

    /**
     * 进行角色验证
     * @return
     */
//    @RequiresRoles("admin1") //表示当前请求的主体必须具备admin1角色才可以访问该接口,否则会报错
    @RequestMapping(value = "testRole1",method =RequestMethod.GET)
    @ResponseBody
    public String testRoles1(){
        return " testRoles1 success";
    }

    ////////////////////////////////////////shiro内置的权限验证 end//////////////////////////////////////

    ////////////////////////////////////////shiro过滤器////////////////////////////////////////

    /**
     * shiro内置过滤器分为认证过滤器和授权过滤器。过滤器配置信息在xml配置文件中设置
     *
     * shiro内置认证过滤器有anon、authBasic、authc、user、logout。<br>
     *  (1)anon：不需要任何认证、可以直接访问接口<br>
     *  (2)authBasic: 表示需要httpBasic认证才能访问接口<br>
     *  (3) authc:必须认证后才可以访问接口<br>
     *  (4)user：只要被shiro记住登录状态的用户都可以访问，这里包括认证和未认证的用户<br>
     *  (5)logout:退出<br>
     *
     *      <br>
     * Shiro内置的授权过滤器与 perms、roles、ssl、port。其中ssl和port使用的较少<br>
     * (1)	perms perms[权限名称] 表示用户拥有相应的权限才可以访问接口<br>
     * (2)	roles：roles[角色名] 表示用户拥有相应的角色才可以访问接口<br>
     * (3)	ssl：表示需要安全的URL请求才可以访问接口，一般为https协议<br>
     * (4)	port port[端口号] ：表示必须为特定端口才可以访问
     * @return
     */
    @RequestMapping(value = "testPerms",method =RequestMethod.GET)
    @ResponseBody
    public String testPerms(){
        return " testPerms success";
    }

    @RequestMapping(value = "testPerms1",method =RequestMethod.GET)
    @ResponseBody
    public String testPerms1(){
        return " testPerms1 success";
    }

}
