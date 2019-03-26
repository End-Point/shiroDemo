package com.liufei.session;

import com.liufei.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

import javax.sound.midi.Soundbank;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * shiro的session管理：可以不借助web容器的情况下使用session：
 * 通过reids实现session共享，需要重写shiro内置的sessionDAO中的方法，主要是重写增删改查session的功能，
 * 然后在配置文件中配置以下sessionManager。
 *
 * 为啥实现session共享：如果项目使用的是分布式，需要在多台服务器之间共同使用同一个session，所以需要将session放入redis中，
 * 可以让多台服务器共同获取。
 *
 */
public class RedisSessionDAO  extends AbstractSessionDAO {
    //注入jedis
    @Autowired
    private JedisUtil jedisUtil;

    //给session名称添加前缀
    private final String shiro_session_prefix ="my_session";


    /**
     * 创建session，将数据存入指定的sessionId中。session信息是shiro框架自动生成的
     * @param session
     * @return
     */
    protected Serializable doCreate(Session session) {
        //获取sessionid
        Serializable sessionId = generateSessionId(session);
        //将sessionId和session进行绑定，不然无法通过该sessionId找到session
        assignSessionId(session,sessionId);
        //将session存入redis
        Boolean uodateSession = this.saveSession(session);
        //通过sessionId将数据保存到该session中，可以使用sessionId获取
        return sessionId;
    }

    /**
     * 通过sessionId获取session信息。session信息是shiro框架自动生成的，每登录一次，会进入5-7遍该方法，
     *  所以，我们要自定义AbstractSessionDAO，提高效率。
     * @param sessionId sessionId值，通过sessionId可以获取到该session的信息
     * @return
     */
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("进入了doReadSession方法，所在位置："+RedisSessionDAO.class);
        if(sessionId == null){
            return null;
        }
        //通过sessionId获取到value
        byte [] key = this.getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        //使用反序列化，将字节数组(byte[])类型的value转成session对象
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * 修改session信息。session信息是shiro框架自动生成的
     * @param session
     * @throws UnknownSessionException
     */
    public void update(Session session) throws UnknownSessionException {
        //将session存入redis
        Boolean updateSession = this.saveSession(session);
        System.out.println("保存状态"+updateSession+"   所在位置："+RedisSessionDAO.class);
    }

    public void delete(Session session) {
        if(session.getId() == null || session == null){
            return ;
        }
        byte[] key = this.getKey(session.getId().toString());
        Long result = jedisUtil.delete(key);
        System.out.println("删除成功"+result);
    }

    /**
     * 获取指定存活的session，因为存活的都存在redis中，超时的都会删除掉，所以直接查询redis中的key，就可以得知存活的key的数量
     * @return
     */
    public Collection<Session> getActiveSessions() {
        //获取存活的key
       Set<byte[]> resultKey =  jedisUtil.getKeys(shiro_session_prefix);
       //创建Set<session>的对象，存放未超时的key
        Set<Session> sessionKey = new HashSet<Session>();
       if(resultKey == null){
           return sessionKey;
       }
       Iterator<byte[]> iterator = resultKey.iterator();
       while (iterator.hasNext()){
           byte[] byteIterator = iterator.next();
           //通过key取出它的value，并反序列化为session类型
           Session session = (Session) SerializationUtils.deserialize(byteIterator);
           //将取出的value值存入到 Set<Session> sessionKey集合中。
           sessionKey.add(session);
       }

        return sessionKey;
    }

    /////////////////////以下是自定义封装的方法//////////////////

    /**
     * 将session信息转成二进制存入redis
     * @param key 存储session信息的sessionid
     * @return
     */
    private byte[] getKey(String key){
        byte[] bytes = (shiro_session_prefix+key).getBytes();
        return bytes;
    }

    /**
     * 保存session和修改session的方法
     * @param session session信息
     * @return true保存成功，false保存失败
     */
    private boolean saveSession(Session session){
        if(session != null && session.getId() != null) {
            //将sessionId转成二进制存入redis
            byte[] key = this.getKey(session.getId().toString());
            //通过序列化将session信息转为二进制数据存入redis
            byte[] value = SerializationUtils.serialize(session);
            try {
                //存入dedis
                jedisUtil.set(key, value);
                //设置超时时间为600秒
                jedisUtil.expire(key, 600);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;

    }

}
