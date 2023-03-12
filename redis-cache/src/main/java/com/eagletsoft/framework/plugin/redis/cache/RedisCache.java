package com.eagletsoft.framework.plugin.redis.cache;

import com.eagletsoft.boot.framework.cache.CacheData;
import com.eagletsoft.boot.framework.cache.ICache;
import com.eagletsoft.boot.framework.common.errors.ServiceException;
import com.eagletsoft.boot.framework.common.errors.StandardErrors;
import com.eagletsoft.boot.framework.common.utils.JsonUtils;
import com.eagletsoft.framework.plugin.redis.client.IRedisClient;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisCache implements ICache {
    private Logger LOG = LoggerFactory.getLogger(RedisCache.class);

    @Autowired private IRedisClient redisClient;

    @Override
    public CacheData get(String sector, String key, Object query) {
        try {
            String cacheKey = makeCacheId(sector, key, query);
            CacheData data = (CacheData)redisClient.get(cacheKey);
            return data;
        } catch (Exception ex) {
            LOG.error("Error happened in cache", ex);
            return null;
        }
    }

    @Override
    public boolean set(String sector, String key, Object query, Object value, int secondsToLive) {
        try {
            String cacheKey = makeCacheId(sector, key, query);
            redisClient.set(cacheKey, value, secondsToLive);
            return true;
        } catch (Exception ex) {
            LOG.error("Error happened in cache", ex);
            return false;
        }
    }

    @Override
    public void expiredAll() {
        throw new ServiceException(StandardErrors.INTERNAL_ERROR.getStatus(), "Unsupported Operation!");
    }


    public String makeCacheId(String sector, String key, Object query)
    {
        StringBuffer cacheId = new StringBuffer("cache://");
        cacheId.append(sector).append("/").append(key).append("/");
        if (null != query)
        {
            ObjectMapper mapper = JsonUtils.createMapper();
            try
            {
                cacheId.append(mapper.writeValueAsString(query));
            }
            catch(Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
        return cacheId.toString();
    }
}
