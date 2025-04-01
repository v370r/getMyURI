package com.getmyuri.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.getmyuri.model.ClickMetric;
import com.getmyuri.repository.ClickMetricRepository;

import jakarta.transaction.Transactional;

@Service
public class RedisToPostgresFlusher {

    @Autowired
    private RedisClickService redisClickService;

    @Autowired
    private ClickMetricRepository clickMetricRepository;

    @Value("${redis.flush.interval}")
    private long flushInterval;

    @Scheduled(fixedRateString = "${redis.flush.interval}")
    @Transactional
    public void flushClicksToPostgres() {
        Map<String, Long> clicks = redisClickService.getAndClearClicks();

        for (Map.Entry<String, Long> entry : clicks.entrySet()) {
            String[] reg = entry.getKey().split(":", 2);
            String username = Optional.ofNullable(reg[0]).orElse("default");
            String alias = reg[1];

            if (username.trim().isEmpty())
                username = "default";

            clickMetricRepository.upsertClickMetric(
                    username,
                    alias,
                    entry.getValue(),
                    LocalDateTime.now());
        }

        System.out.println("Flushed " + clicks.size() + " click(s) to PostgreSQL.");
    }
}
