package com.getmyuri.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.model.DataObjectFormat;
import com.getmyuri.service.redis.CounterService;
import com.getmyuri.util.Base67Encoder;

@Service
public class DefaultUrlService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Base67Encoder base67Encoder;

    @Autowired
    private CounterService counterService;

    @Value("${shortlink.default.ttl}")
    private String defaultTtlString;

    public String createShortUrl(LinkDTO linkDTO) {

        // Step 1: Atomic counter from Redis
        Long counter = counterService.getNextId();

        // Step 2: Convert counter to base-67 code
        String shortCode = base67Encoder.encode(counter);
        Date startTime = new Date();
        Date expiresAt = calculateExpiryFromNow(defaultTtlString);
        // Step 3: Save in MongoDB with username = 'default'
        DataObjectFormat root = DataObjectFormat.builder().alias(shortCode).link(linkDTO.getLink()).username("default")
                .startTime(startTime)
                .expiresAt(expiresAt)
                .password("").build();
        mongoTemplate.insert(root, "links");

        redisTemplate.opsForValue().set("clicks:default:" + shortCode, "0");

        return shortCode;
    }

    public Date calculateExpiryFromNow(String ttlStr) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        Pattern pattern = Pattern.compile("(\\d+)([Mdhm])"); // Case-sensitive
        Matcher matcher = pattern.matcher(ttlStr);

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "M":
                    now = now.plusMonths(value);
                    break;
                case "d":
                    now = now.plusDays(value);
                    break;
                case "h":
                    now = now.plusHours(value);
                    break;
                case "m":
                    now = now.plusMinutes(value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid TTL unit: " + unit);
            }
        }

        return Date.from(now.toInstant());
    }

}
