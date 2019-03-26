package com.liufei.util;

import org.apache.shiro.realm.text.IniRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 通过访问工具jedis访问redis
 */
@Component
public class JedisUtil {

    //通过jedis连接池获取redis连接,redis连接信息都在spring-redis.xml中
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取redis连接
     * @return 返回redis连接信息
     */
    private Jedis getResource(){
        return jedisPool.getResource();
    }

    /**
     * 把数据存入redis
     * @param key redis的key值
     * @param value redis的value
     * @return
     */
    public byte[] set(byte[] key,byte[] value){
        //首先获取连接
        Jedis jedis = this.getResource();

        //使用dedis工具方法，将数据存入redis中
        try {
            jedis.set(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            jedis.close();
        }
        //添加成功返回添加的值。
        return value;
    }

    /**
     * 通过key值获取value
     * @param key redis的key值
     * @return
     */
    public byte[] get(byte[] key){
        //首先获取连接
        Jedis jedis = this.getResource();
        byte[] resultValue = null;
        //使用dedis工具方法，将数据存入redis中
        try {
            resultValue = jedis.get(key);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            jedis.close();
        }
        //添加成功返回添加的值。
        return resultValue;
    }

    /**
     * 删除redis的key/value
     * @param key
     * @return
     */
    public Long  delete(byte[] key){
        //获取redis连接
        Jedis jedis = this.getResource();
        Long resultData = null;
        try {
            resultData = jedis.del(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return resultData;
    }

    /**
     * 获取所有前缀名为shiro_session_prefix的key值
     * @param shiro_session_prefix
     * @return
     */
    public Set<byte[]> getKeys(String shiro_session_prefix){
        //连接redis
       Jedis jedis =   this.getResource();
        Set<byte[]> set = null;
       try {
           //获取所有前缀是my_session的key/value。 匹配格式：前缀名 * ；返回值为Set<String>
           /*
           * jedis.keys()如果传入的是字符串，返回值为Set<String>类型
           *              如果传入的是byte[]，返回值为Set<byte[]>类型
           * */
           set = jedis.keys((shiro_session_prefix + " *").getBytes());
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
       return set;

    }

    /**
     * 设置key的超时时间
     * @param key key值
     * @param timeOut 超时时间，以秒为单位
     */
    public void expire(byte[] key, Integer timeOut){
        //首先获取连接
        Jedis jedis = this.getResource();

        try {
            //设置key的超时时间，以秒为为单位
            jedis.expire(key,timeOut);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }

    }
}
