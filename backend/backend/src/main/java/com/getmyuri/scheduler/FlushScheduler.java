package com.getmyuri.scheduler;

import com.getmyuri.service.redis.RedisClickService;
import com.getmyuri.repository.ClickMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class FlushScheduler {

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
        Map<String, Long> clickMap = redisClickService.getAndClearClicks();

        for (Map.Entry<String, Long> entry : clickMap.entrySet()) {
            String[] parts = entry.getKey().split(":"); // "username:alias"
            if (parts.length == 2) {
                String username = parts[0];
                String alias = parts[1];
                Long count = entry.getValue();

                clickMetricRepository.upsertClickMetric(username, alias, count, LocalDateTime.now());
            }
        }

        System.out.println(" [Scheduler] Click metrics flushed at: " + LocalDateTime.now());
    }
}
