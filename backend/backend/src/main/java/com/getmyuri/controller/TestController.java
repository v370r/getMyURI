package com.getmyuri.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Clear the Redis counter (base67:counter or shorturl:counter)
    @PostMapping("/reset-counter")
    public ResponseEntity<String> resetCounter(@RequestParam String secret) {
        if (!"vetor".equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Unauthorized");
        }
        redisTemplate.opsForValue().set("base67:counter", "0");
        return ResponseEntity.ok("✅ Redis counter reset to 0");
    }

    // Optional: Clear all clicks
    @DeleteMapping("/clear-clicks")
    public ResponseEntity<String> clearClicks() {
        Set<String> keys = redisTemplate.keys("clicks:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return ResponseEntity.ok("🧹 Clicks cleared");
    }
}
