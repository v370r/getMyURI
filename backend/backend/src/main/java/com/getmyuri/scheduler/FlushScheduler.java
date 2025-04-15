package com.getmyuri.scheduler;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.getmyuri.repository.ClickMetricRepository;
import com.getmyuri.service.redis.RedisClickService;

@Component
public class FlushScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FlushScheduler.class); 

    @Autowired
    private RedisClickService redisClickService;

    @Autowired
    private ClickMetricRepository clickMetricRepository;

    /**
     * This scheduled task flushes Redis click counts to PostgreSQL every 5 minutes.
     * Redis keys are expected to be in the format: "click:username:alias"
     */
    @Scheduled(fixedRateString = "${redis.flush.interval}") // 5 minutes
    public void flushClicks() {
        logger.info("🔄 Flushing click metrics from Redis to PostgreSQL...");
        
        Map<String, Long> clickMap = redisClickService.getAndClearClicks();
        int flushedCount = 0;

        for (Map.Entry<String, Long> entry : clickMap.entrySet()) {
            String[] parts = entry.getKey().split(":"); // "username:alias"
            if (parts.length == 2) {
                String username = parts[0];
                String alias = parts[1];
                Long count = entry.getValue();

                clickMetricRepository.upsertClickMetric(username, alias, count, LocalDateTime.now());
                flushedCount++;
                logger.debug("Flushed: username={}, alias={}, count={}", username, alias, count);
            } else {
                logger.warn("Invalid Redis key format: {}", entry.getKey());
            }
        }

        logger.info(" Flushed {} click metric(s) at {}", flushedCount, LocalDateTime.now());
    }
}
