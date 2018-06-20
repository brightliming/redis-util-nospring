package com.troila.redis.sharded;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.troila.redis.JedisInterface;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/27.
 */
public class ShardedRedis implements JedisInterface {

    private static final String DEFAULT_REDIS_SEPARATOR = ";";

    private static final String HOST_PORT_SEPARATOR = ":";

    private ShardedJedisPool shardedJedisPool = null;

    public ShardedRedis(Properties properties) {
        initialShardedPool(properties);
    }

    private void initialShardedPool(Properties properties) {
        // 操作超时时间,默认2秒
        int timeout = NumberUtils.toInt(properties.getProperty("redis.timeout"), 2000);
        // ShardedJedis池最大连接数总数，默认8
        int maxTotal = NumberUtils.toInt(properties.getProperty("redis.ShardedJedisPoolConfig.maxTotal"), 8);
        // ShardedJedis池最大空闲连接数，默认8
        int maxIdle = NumberUtils.toInt(properties.getProperty("redis.ShardedJedisPoolConfig.maxIdle"), 8);
        // ShardedJedis池最少空闲连接数
        int minIdle = NumberUtils.toInt(properties.getProperty("redis.ShardedJedisPoolConfig.minIdle"), 0);
        // ShardedJedis池没有对象返回时，最大等待时间单位为毫秒
        long maxWaitMillis = NumberUtils.toLong(properties.getProperty("redis.ShardedJedisPoolConfig.maxWaitTime"), -1);
        // 在borrow一个ShardedJedis实例时，是否提前进行validate操作
        boolean testOnBorrow = Boolean.parseBoolean(properties.getProperty("redis.ShardedJedisPoolConfig.testOnBorrow"));
        // 权限验证auth
        String auth = properties.getProperty("redis.jedisPoolConfig.auth");

        // 设置ShardedJedis连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig ();
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
        List<JedisShardInfo> shardedPoolList = new ArrayList<JedisShardInfo>();
        for (String redisUrl : redisUrls.split(DEFAULT_REDIS_SEPARATOR)) {
            String[] redisUrlInfo = redisUrl.split(HOST_PORT_SEPARATOR);
            JedisShardInfo ShardedJedisinfo = new JedisShardInfo(redisUrlInfo[0], Integer.parseInt(redisUrlInfo[1]), timeout);
            if(StringUtils.isNotBlank(auth)) {
            	ShardedJedisinfo.setPassword(auth);
            }
            shardedPoolList.add(ShardedJedisinfo);
        }

        // 构造池
        this.shardedJedisPool = new ShardedJedisPool(poolConfig, shardedPoolList, Hashing.MURMUR_HASH);
    }

    public <T> T execute(String key, ShardedRedisExecutor<T> executor) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        T result = null;
        try {
            result = executor.execute(shardedJedis);
        } finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
        return result;
    }

    @Override
    public String set(final String key, final String value) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.set(key, value);
            }
        });
    }

    @Override
    public String set(final String key,final String value,final String nxxx,final String expx,final long time) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.set(key, value, nxxx, expx, time);
            }
        });
    }

    @Override
    public String get(final String key) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.get(key);
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return execute(key, new ShardedRedisExecutor<Boolean>() {
            @Override
            public Boolean execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.exists(key);
            }
        });
    }

    @Override
    public Long setnx(final String key,final String value) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.setnx(key, value);
            }
        });
    }

    @Override
    public String setex(final String key,final int seconds,final String value) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.setex(key, seconds, value);
            }
        });
    }

    @Override
    public Long expire(final String key,final int seconds) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.expire(key, seconds);
            }
        });
    }

    @Override
    public Long incr(final String key) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.incr(key);
            }
        });
    }

    @Override
    public Long decr(final String key) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.decr(key);
            }
        });
    }

    @Override
    public Long hset(final String key,final String field,final String value) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.hset(key, field, value);
            }
        });
    }

    @Override
    public String hget(final String key,final String field) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.hget(key, field);
            }
        });
    }

    @Override
    public String hmset(final String key,final Map<String, String> hash) {
        return execute(key, new ShardedRedisExecutor<String>() {
            @Override
            public String execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.hmset(key, hash);
            }
        });
    }

    @Override
    public List<String> hmget(final String key,final String... fields) {
        return execute(key, new ShardedRedisExecutor<List<String>>() {
            @Override
            public List<String> execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.hmget(key, fields);
            }
        });
    }

    @Override
    public Long del(final String key) {
        return execute(key, new ShardedRedisExecutor<Long>() {
            @Override
            public Long execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.del(key);
            }
        });
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        return execute(key, new ShardedRedisExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(ShardedJedis ShardedJedis) {
                return ShardedJedis.hgetAll(key);
            }
        });
    }

    @Override
    public void destroy() {
        this.shardedJedisPool.close();
    }
}
