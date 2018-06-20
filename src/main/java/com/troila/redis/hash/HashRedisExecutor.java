package com.troila.redis.hash;

import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 2017/9/27.
 */
public interface HashRedisExecutor<T> {
    T execute(Jedis jedis);
}
