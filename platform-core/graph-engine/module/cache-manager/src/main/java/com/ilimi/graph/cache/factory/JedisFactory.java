package com.ilimi.graph.cache.factory;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.ilimi.graph.cache.exception.GraphCacheErrorCodes;
import com.ilimi.graph.common.exception.ServerException;
import com.ilimi.graph.common.mgr.Configuration;

public class JedisFactory {

    private static JedisPool jedisPool;

    private static int maxConnections = 128;
    private static String host = "localhost";
    private static int port = 6379;

    static {
        try {
            InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("graph.properties");
            if (null != inputStream) {
                Properties props = new Properties();
                props.load(inputStream);
                String redisHost = props.getProperty("redis.host");
                if (StringUtils.isNotBlank(redisHost))
                    host = redisHost;
                String redisPort = props.getProperty("redis.port");
                if (StringUtils.isNotBlank(redisPort)) {
                    try {
                        port = Integer.parseInt(redisPort);
                    } catch (Exception e) {
                    }
                }
                String redisMaxConn = props.getProperty("redis.maxConnections");
                if (StringUtils.isNotBlank(redisMaxConn)) {
                    try {
                        maxConnections = Integer.parseInt(redisMaxConn);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setBlockWhenExhausted(true);
        jedisPool = new JedisPool(config, host, port);
    }

    public static Jedis getRedisConncetion() {
        try {
            Jedis jedis = jedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_CONNECTION_ERROR.name(), e.getMessage());
        }

    }

    public static void returnConnection(Jedis jedis) {
        try {
            if (null != jedis)
                jedisPool.returnResource(jedis);
        } catch (Exception e) {
            throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_CONNECTION_ERROR.name(), e.getMessage());
        }
    }
}
