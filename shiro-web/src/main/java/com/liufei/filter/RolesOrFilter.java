package com.liufei.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;



/**
 * 自定义shiro过滤器：
 * 自定义实现在多个角色中满足其中一个角色条件即可访问接口的过滤器,并加入配置文件中(spring.xml)，让项目发布时，自动创建对象
 * AuthorizationFilter：这是授权相关的filter。若想继承认证相关的filter需要继承authenticationFilter
 * 自定义权限和这个差不多，按照这个写就行
 *
 */
public class RolesOrFilter extends AuthorizationFilter {

    /**
     *
     * @param servletRequest
     * @param servletResponse
     * @param o 角色信息
     * @return
     * @throws Exception
     */
    protected boolean isAccessAllowed(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, Object o) throws Exception {
        //首先要获取主体,包括角色、权限，调用父类AuthorizationFilter的getSubject方法获取主体信息
        Subject subject = getSubject(servletRequest,servletResponse);
        //获取过滤器设置的指定角色，就是roles["角色1,角色2"]
        String[] roles = (String[]) o;
        //如果设置的角色是空，说明没有设置角色权限，返回true
        if(roles == null||roles.length == 0){
            return true;
        }

        //遍历指定角色的信息，如果角色信息和主体的任一角色信息相同，则返回true
        for(String role: roles){
            //subject.hasRole(role)方法 判断是否是该角色
            if(subject.hasRole(role)){
                return true;
            }
        }
        //如果都没有 返回false
        return false;
    }
}
