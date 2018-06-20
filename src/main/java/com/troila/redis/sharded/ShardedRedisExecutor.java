package com.troila.redis.sharded;

import redis.clients.jedis.ShardedJedis;

/**
 * Created by Administrator on 2017/9/27.
 */
public interface ShardedRedisExecutor<T> {
    T execute(ShardedJedis jedis);
}
