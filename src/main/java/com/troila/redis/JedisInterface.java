package com.troila.redis;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/27.
 */
public interface JedisInterface {

    public String set(String key, String value);

    public String set(String key, String value, String nxxx, String expx, long time);

    public String get(String key);
    
    public byte[] getByte(byte[] key);

    public Boolean exists(String key);

    public Long setnx(String key, String value);

    public String setex(String key, int seconds, String value);

    public Long expire(String key, int seconds);

    public Long incr(String key);

    public Long decr(String key);

    public Long hset(String key, String field, String value);

    public String hget(String key, String field);

    public String hmset(String key, Map<String, String> hash);

    public List<String> hmget(String key, String... fields);

    public Long del(String key);

    public Map<String, String> hgetAll(String key);

    public void destroy();
}
