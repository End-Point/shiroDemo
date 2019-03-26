package com.liufei.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * 自定义sessionDAO,提高开发效率
 */
public class CustomSessionDAO extends DefaultWebSessionManager {

    /**
     * 重写父类的方法，将sessionId和对应的session放入request中，这样可以直接从request中获取，不需要每次都从redis中获取。提高了效率
      * @param sessionKey 存储了request对象
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        //获取sessionid
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        //从sessionKey中获取request请求
        if(sessionKey instanceof WebSessionKey){
            //因为sessionKey中存储了request对象，所以，可以将sessionKey强转为WebSessionKey然后获取request对象
            request = ((WebSessionKey)sessionKey).getServletRequest();
        }
        //从request从获取session对象,如果获取到，就返回
        if(request != null && sessionId != null){
          Session resultSession = (Session) request.getAttribute(sessionId.toString());
            if(resultSession != null){
                return resultSession;
            }
        }

        //如果从request中获取不到对象,就从redis中取出session对象，然后放入request中
         //获取session对象
        Session session = super.retrieveSession(sessionKey);
        if(request != null && sessionId != null){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
