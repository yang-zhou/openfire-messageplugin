package org.igniterealtime.openfire.messageplugin.utils;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils {
	private static final Logger Log = LoggerFactory.getLogger(JedisPoolUtils.class);
    public static String CHAT_MESSAGE_TOPIC = "message/chat/one2one";
    public static String GROUP_CHAT_MESSAGE_TOPIC = "message/chat/group";
	
	/**
	 * 初始化连接池
	 * 
	 * @return
	 */
	private static JedisPool init() {
		String configFilePath = JedisPoolUtils.class.getResource("/redis.properties").toString();
		Properties properties = PropertiesUtils.getProperties("/redis.properties");
		JedisPool pool = null;
		
		Log.info("=======================================================");
		if (properties != null) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			Log.info("Get Redis Pool Config File From : " + configFilePath);
			String hostStr = properties.getProperty("redis.host");
			String portStr = properties.getProperty("redis.port");
			String passwordStr = properties.getProperty("redis.password");
			String timeOutStr = properties.getProperty("redis.timeout");
			String maxActiveStr = properties.getProperty("redis.pool.maxActive");
			if(maxActiveStr != null) {
				jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(maxActiveStr));	
			}
			String maxIdleStr = properties.getProperty("redis.pool.maxIdle");
			if(maxIdleStr != null) {
				jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(maxIdleStr));	
			}
			String minIdleStr = properties.getProperty("redis.pool.minIdle");
			if(minIdleStr != null) {
				jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(minIdleStr));	
			}
			String maxWaitStr = properties.getProperty("redis.pool.maxWait");
			if(maxWaitStr != null) {
				jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(maxWaitStr));	
			}
			String testOnBorrowStr = properties.getProperty("redis.pool.testOnBorrow");
			if(testOnBorrowStr != null) {
				jedisPoolConfig.setTestOnBorrow(Boolean.valueOf(testOnBorrowStr));	
			}
			String testOnReturnStr = properties.getProperty("redis.pool.testOnReturn");
			if(testOnReturnStr != null) {
				jedisPoolConfig.setTestOnReturn(Boolean.valueOf(testOnReturnStr));	
			}
			pool = new JedisPool(jedisPoolConfig, hostStr, Integer.valueOf(portStr), Integer.valueOf(timeOutStr), passwordStr);
		} else {
			throw new RuntimeException("Get Redis Pool Config File From : " + configFilePath + " Failed ");
		}
		Log.info("=======================================================");
		return pool;
	}

	public static void singletonStart() {
        getInstance();
    }
	
	/**
	 * 单例模式创建JedisPool实例
	 *
	 * @return JedisPool
	 */
	private volatile static JedisPool singleton;
	public static JedisPool getInstance() {
		if (singleton == null) {
			synchronized (HazelcastUtils.class) {
				if (singleton == null) {
					singleton = init();
				}
			}
		}
		return singleton;
	}

	public static Jedis tryGetResource() {
		Jedis jedis = null;
		try {
			jedis = getInstance().getResource();
		} catch (Exception e) {
			if(jedis != null) getInstance().returnBrokenResource(jedis);	
		}
		return jedis;
	}
	
	public static void tryPush(String msg) {
		Jedis jedis = tryGetResource();
		if(jedis != null) {
			jedis.publish(CHAT_MESSAGE_TOPIC, msg);
			// 使用后关闭，避免耗尽连接
			jedis.close();
		}
	}
	
	/**
	 * 检查是否有Redis连接池
	 * @return
	 */
	public static boolean checkRedis() {
		boolean ok = false;
		try {
			JedisPool pool = getInstance();
			if(pool != null) ok = true;
		} catch (Exception e) {}
		return ok;
	}
}
