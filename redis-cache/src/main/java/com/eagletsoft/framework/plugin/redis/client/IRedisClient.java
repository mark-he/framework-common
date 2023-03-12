package com.eagletsoft.framework.plugin.redis.client;

public interface IRedisClient {
	
	void set(String key, Object value);
	void set(String key, Object value, int secondsToLive);
	Object get(String key);

	void set(String key, byte[] value);
	void set(String key, byte[] value, int secondsToLive);
	byte[] getBytes(String key);
	
	boolean exists(String key);
	void remove(String key);

	void destroy();
}
