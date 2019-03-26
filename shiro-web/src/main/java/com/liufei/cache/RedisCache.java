package com.liufei.cache;

import com.liufei.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.Set;

/**
 * shiro的缓存管理：第二步：
 * 重写cache接口中的方法，用来操作缓存数据，和自定义sessionDAO相似
 */
@Component
public class RedisCache<K,V> implements Cache<K,V> {

    @Autowired
    private JedisUtil jedisUtil;
    private final String CACHE_PREFIX = "my_cache"; //key值的前缀
    /**
     *根据key值从缓存中获取value
     * @param k key值
     * @return value值
     * @throws CacheException
     */
    public V get(K k) throws CacheException {
        byte[] value = this.getKey(k);
        if(value != null){
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    /**
     * 向缓存中添加数据
     * @param k
     * @param v
     * @return
     * @throws CacheException
     */
    public V put(K k, V v) throws CacheException {
        byte[] key = this.getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        //设置超时时间，单位是秒
        jedisUtil.expire(key,600);
        return v;
    }

    /**
     * 根据key值删除，
     * @param k
     * @return 返回被删除的value
     * @throws CacheException
     */
    public V remove(K k) throws CacheException {
        byte[] key = this.getKey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.delete(key);
        //返回被删除的value值
        return (V) SerializationUtils.deserialize(value);
    }

    /**
     * 清除缓存中的所有事数据
     * @throws CacheException
     */
    public void clear() throws CacheException {

    }

    public int size() {
        return 0;
    }

    /**
     * 获取多gekey/value
     * @return
     */
    public Set<K> keys() {
        return null;
    }

    public Collection<V> values() {
        return null;
    }

    /**
     * 将key值序列化,并通过key获取value
     * @param key
     * @return
     */
    private byte[] getKey(K key){
        //如果key值为字符串，使用前缀+字符串，将其序列化
        if(key instanceof String){
            return (CACHE_PREFIX + key).getBytes();
        }
        return SerializationUtils.serialize(key);
    }
}
