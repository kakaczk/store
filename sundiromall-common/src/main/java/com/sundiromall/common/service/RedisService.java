package com.sundiromall.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {

    //spring环境中有就注入，没有就忽略
    @Autowired(required=false)
    private ShardedJedisPool shardedJedisPool;
    
    /**
     * 抽取的通用
     * @param function
     * @return
     */
    private <T> T execute(Function<ShardedJedis, T> function) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
            return function.callback(shardedJedis);
        } finally {
            if(null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
    }
    
    /**
     * 设置值
     * @param key
     * @param value
     * @return
     */
    public String set(final String key, final String value) {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis shardedJedis) {
                return shardedJedis.set(key, value);
            }
        });
    }
    
    /**
     * 获取值
     * @param key
     * @return
     */
    public String get(final String key) {
        return this.execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                return jedis.get(key);
            }
        });
    }
   
    /**
     * 删除值
     * @param key
     * @return
     */
    public Long del(final String key) {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                return jedis.del(key);
            }
        });
    }
    
    /**
     * 设置生存时间
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final Integer seconds) {
        return this.execute(new Function<ShardedJedis, Long>() {
            @Override
            public Long callback(ShardedJedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }
    
    /**
     * 设置值及生存时间
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public String set(final String key, final String value, final Integer seconds) {
        return execute(new Function<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis jedis) {
                String result = jedis.set(key, value);
                jedis.expire(key, seconds);
                return result;
            }
        });
    }
}
