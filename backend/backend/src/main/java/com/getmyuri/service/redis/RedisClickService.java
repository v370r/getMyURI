package com.getmyuri.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RedisClickService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CLICK_PREFIX = "click:";

    public void incrementClick(String username, String aliasPath) {
        redisTemplate.opsForValue().increment("click:" + username + ":" + aliasPath);
    }

    public Map<String, Long> getAndClearClicks() {
        Set<String> keys = redisTemplate.keys(CLICK_PREFIX + "*");
        Map<String, Long> result = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    String alias = key.replaceFirst(CLICK_PREFIX, "");
                    result.put(alias, Long.parseLong(value));
                    redisTemplate.delete(key); // remove after flush
                }
            }
        }

        return result;
    }

    public Map<String, Long> getAllClicks() {
        Set<String> keys = redisTemplate.keys(CLICK_PREFIX + "*");
        Map<String, Long> result = new HashMap<>();

        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    String alias = key.replaceFirst(CLICK_PREFIX, "");
                    result.put(alias, Long.parseLong(value));
                }
            }
        }

        return result;
    }
}
