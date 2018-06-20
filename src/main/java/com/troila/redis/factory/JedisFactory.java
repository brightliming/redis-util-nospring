package com.troila.redis.factory;

import java.util.Properties;

import com.troila.redis.JedisInterface;
import com.troila.redis.hash.HashRedis;
import com.troila.redis.sharded.ShardedRedis;

/**
 * Created by Administrator on 2017/9/27.
 */
public class JedisFactory{
    
    private static JedisInterface jedis = null; 

    public static JedisInterface getJedis(Properties properties){
    	if(jedis == null) {
    		synchronized(JedisFactory.class) {
    			if(jedis == null) {
    				String redisPoolType = properties.getProperty("redis.jedisPoolConfig.auth");
                    if("hash".equals(redisPoolType)){
                    	jedis = new HashRedis(properties);
                    }else{
                    	jedis = new ShardedRedis(properties);
                    }
    			}
    			
    		}
    		
    	}
        return jedis;
    }

}
