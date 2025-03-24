package com.getmyuri.service;

import java.time.LocalDateTime;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.getmyuri.model.DataObjectFormat;
import com.getmyuri.util.Base67Encoder;

@Service
public class DefaultUrlService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Base67Encoder base67Encoder;

    public String createShortUrl(String longUrl) {
        // Step 1: Atomic counter from Redis
        Long counter = redisTemplate.opsForValue().increment("shorturl:counter");

        if (counter == null) {
            throw new IllegalStateException("Redis counter increment failed");
        }

        // Step 2: Convert counter to base-67 code
        String shortCode = base67Encoder.encode(counter);

        // Step 3: Save in MongoDB with username = 'default'
        DataObjectFormat root = DataObjectFormat.builder().alias(shortCode).link(longUrl).username("default")
                .password("").build();
        mongoTemplate.insert(root, "links");

        redisTemplate.opsForValue().set("clicks:default:" + shortCode, "0");

        return shortCode;
    }
}
