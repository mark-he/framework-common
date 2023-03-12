package com.eagletsoft.framework.plugin.redis.client.impl;


import com.eagletsoft.framework.plugin.redis.client.IRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class DefaultRedisClient implements IRedisClient {
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void set(String key, Object value, int secondsToLive) {
		redisTemplate.opsForValue().set(key, value, secondsToLive, TimeUnit.SECONDS);
	}

	@Override
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void set(String key, byte[] value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void set(String key, byte[] value, int secondsToLive) {
		redisTemplate.opsForValue().set(key, value, secondsToLive, TimeUnit.SECONDS);
	}

	@Override
	public byte[] getBytes(String key) {
		return (byte[])redisTemplate.opsForValue().get(key);
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void destroy() {
		redisTemplate.discard();
	}
}
