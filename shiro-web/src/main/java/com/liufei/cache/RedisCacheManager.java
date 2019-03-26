package com.liufei.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * shiro的缓存管理：第一步：
 * 自定义缓存，通过实现shiro内置CacheManager重写其中的方法实现自定义缓存
 */
public class RedisCacheManager implements CacheManager {

    //注入缓存管理的bean
    @Autowired
    private RedisCache redisCache;

    /**
     * 获取缓存的方法
     * @param s cache名称
     * @param <K>
     * @param <V>
     * @return 返回cache实例对象
     * @throws CacheException
     */
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return redisCache;
    }
}
