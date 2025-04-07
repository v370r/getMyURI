package com.getmyuri.service.redis;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    private static final int BLOCK_SIZE = 1000;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Queue<Long> localCounterQueue = new ConcurrentLinkedQueue<>();

    public synchronized long getNextId() {
        if (localCounterQueue.isEmpty()) {
            long start = redisTemplate.opsForValue().increment("base67:counter", BLOCK_SIZE);
            long end = start - BLOCK_SIZE + 1;

            System.out.println("🔁 Redis block fetched: " + end + " to " + start);

            for (long i = end; i <= start; i++) {
                System.out.println("👉 Adding " + i + " to local queue");
                localCounterQueue.add(i);
            }
        }

        long nextId = localCounterQueue.poll();
        System.out.println(" Serving counter: " + nextId);

        return nextId;
    }
}
