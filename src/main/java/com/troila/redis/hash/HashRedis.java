package com.troila.redis.hash;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.troila.redis.JedisInterface;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/27.
 */
public class HashRedis implements JedisInterface{

    private static final String DEFAULT_REDIS_SEPARATOR = ";";

    private static final String HOST_PORT_SEPARATOR = ":";

    private JedisPool[] jedisPools = new JedisPool[0];



    public HashRedis(Properties properties){
        initPool(properties);
    }


    private void initPool(Properties properties){
        // 操作超时时间,默认2秒
        int timeout = NumberUtils.toInt(properties.getProperty("redis.timeout"), 2000);
        // jedis池最大连接数总数，默认8
        int maxTotal = NumberUtils.toInt(properties.getProperty("redis.jedisPoolConfig.maxTotal"), 8);
        // jedis池最大空闲连接数，默认8
        int maxIdle = NumberUtils.toInt(properties.getProperty("redis.jedisPoolConfig.maxIdle"), 8);
        // jedis池最少空闲连接数
        int minIdle = NumberUtils.toInt(properties.getProperty("redis.jedisPoolConfig.minIdle"), 0);
        // jedis池没有对象返回时，最大等待时间单位为毫秒
        long maxWaitMillis = NumberUtils.toLong(properties.getProperty("redis.jedisPoolConfig.maxWaitTime"), -1);
        // 在borrow一个jedis实例时，是否提前进行validate操作
        boolean testOnBorrow = Boolean.parseBoolean(properties.getProperty("redis.jedisPoolConfig.testOnBorrow"));
        //用户权限 auth
        String auth = properties.getProperty("redis.jedisPoolConfig.auth");
        

        // 设置jedis连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);

        // 取得redis的url
        String redisUrls = properties.getProperty("redis.jedisPoolConfig.urls");
        if (redisUrls == null || redisUrls.trim().isEmpty()) {
            throw new IllegalStateException("the urls of redis is not configured");
        }

        // 生成连接池
        List<JedisPool> jedisPoolList = new ArrayList<JedisPool>();
        for (String redisUrl : redisUrls.split(DEFAULT_REDIS_SEPARATOR)) {
            String[] redisUrlInfo = redisUrl.split(HOST_PORT_SEPARATOR);
            JedisPool pool = null;
            if(StringUtils.isNotBlank(auth)) {
            	pool =  new JedisPool(poolConfig, redisUrlInfo[0], Integer.parseInt(redisUrlInfo[1]), timeout,auth);
            }else {
            	pool = new JedisPool(poolConfig, redisUrlInfo[0], Integer.parseInt(redisUrlInfo[1]), timeout);
            }
            jedisPoolList.add(pool);
        }

        jedisPools = jedisPoolList.toArray(jedisPools);
    }

    private <T> T execute(String key, HashRedisExecutor<T> executor) {
        Jedis jedis = jedisPools[(0x7FFFFFFF & key.hashCode()) % jedisPools.length].getResource();
        T result = null;
        try {
            result = executor.execute(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    @Override
    public String set(final String key, final String value) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    @Override
    public String set(final String key,final String value,final String nxxx,final String expx,final long time) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.set(key, value, nxxx, expx, time);
            }
        });
    }

    @Override
    public String get(final String key) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return execute(key, new HashRedisExecutor<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long setnx(final String key,final String value) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    @Override
    public String setex(final String key,final int seconds,final String value) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    @Override
    public Long expire(final String key,final int seconds) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    @Override
    public Long incr(final String key) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    @Override
    public Long decr(final String key) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    @Override
    public Long hset(final String key,final String field,final String value) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    @Override
    public String hget(final String key,final String field) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    @Override
    public String hmset(final String key,final Map<String, String> hash) {
        return execute(key, new HashRedisExecutor<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hmset(key, hash);
            }
        });
    }

    @Override
    public List<String> hmget(final String key,final String... fields) {
        return execute(key, new HashRedisExecutor<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    @Override
    public Long del(final String key) {
        return execute(key, new HashRedisExecutor<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.del(key);
            }
        });
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        return execute(key, new HashRedisExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    @Override
    public void destroy() {
        for (int i = 0; i < jedisPools.length; i++) {
            jedisPools[i].close();
        }
    }
}
