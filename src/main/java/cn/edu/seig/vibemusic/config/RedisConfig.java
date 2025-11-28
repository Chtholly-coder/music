package cn.edu.seig.vibemusic.config;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类
 * 使用Jedis连接池管理Redis连接
 */
public class RedisConfig {

    // 单例Jedis连接池
    private static JedisPool jedisPool;

    // Redis配置参数
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = null; // 如有密码请设置
    private static final int REDIS_DATABASE = 1;
    private static final int REDIS_TIMEOUT = 3000;

    // 静态初始化块，创建连接池
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(20);         // 最大连接数
            config.setMaxIdle(10);          // 最大空闲连接数
            config.setMinIdle(5);           // 最小空闲连接数
            config.setTestOnBorrow(true);   // 获取连接时检测有效性

            if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, REDIS_PASSWORD, REDIS_DATABASE);
            } else {
                jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT, REDIS_TIMEOUT, null, REDIS_DATABASE);
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化Redis连接池失败", e);
        }
    }

    /**
     * 获取Jedis连接池
     * @return Jedis连接池
     */
    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * 关闭连接池
     */
    public static void closePool() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}


