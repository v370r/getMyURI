package com.getmyuri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RedisCounterBackupService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 3600000)
    public void backupRedisCounterToPostgres() {
        String counterValue = redisTemplate.opsForValue().get("shorturl:counter");

        if (counterValue == null) {
            System.err.println("No Redis counter found.");
            return;
        }

        try {
            long counter = Long.parseLong(counterValue);
            jdbcTemplate.update("INSERT INTO url_counter_backup (counter) VALUES (?)", counter);
            System.out.println("Backed up Redis counter: " + counter);
        } catch (Exception ex) {
            System.err.println("Backup failed: " + ex.getMessage());
        }
    }
}
