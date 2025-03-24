package com.getmyuri.service;

import java.time.LocalDateTime;
import java.util.Map;

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
            String[] reg = entry.getKey().split(":");
            String username = reg[0];
            ClickMetric metric = ClickMetric.builder().alias(reg[1]).clickCount(entry.getValue())
                    .username(username)
                    .clickDate(LocalDateTime.now()).build();
            clickMetricRepository.save(metric);
        }

        System.out.println("Flushed " + clicks.size() + " click(s) to PostgreSQL.");
    }
}
