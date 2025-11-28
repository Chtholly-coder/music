package cn.edu.seig.vibemusic.service;

import cn.edu.seig.vibemusic.config.RedisConfig;
import redis.clients.jedis.Jedis;

/**
 * Redis服务类
 * 封装Redis常用操作
 */
public class RedisService {

    /**
     * 设置键值对（带过期时间）
     * @param key 键
     * @param value 值
     * @param seconds 过期时间（秒）
     */
    public void set(String key, String value, int seconds) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    /**
     * 设置键值对（不过期）
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            jedis.set(key, value);
        }
    }

    /**
     * 获取值
     * @param key 键
     * @return 值，不存在返回null
     */
    public String get(String key) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * 删除键
     * @param key 键
     * @return 删除的键数量
     */
    public long delete(String key) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            return jedis.del(key);
        }
    }

    /**
     * 检查键是否存在
     * @param key 键
     * @return true-存在，false-不存在
     */
    public boolean exists(String key) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            return jedis.exists(key);
        }
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param seconds 过期时间（秒）
     */
    public void expire(String key, int seconds) {
        try (Jedis jedis = RedisConfig.getJedisPool().getResource()) {
            jedis.expire(key, seconds);
        }
    }
}


