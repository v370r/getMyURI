package com.getmyuri.healthcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.getmyuri.constants.ServiceConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class ServiceHealthController {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Map<String, String> checkServices(@RequestParam String service) {
        Map<String, String> statusMap = new HashMap<>();
        List<String> servicesToCheck;

        if (ServiceConstants.ALL.equalsIgnoreCase(service.trim())) {
            servicesToCheck = new ArrayList<>(ServiceConstants.KNOWN_SERVICES);
        } else {
            servicesToCheck = Arrays.asList(service.toLowerCase().split(","));
        }

        for (String s : servicesToCheck) {
            switch (s.trim()) {
                case ServiceConstants.REDIS_CLICK:
                    try (RedisConnection connection = redisConnectionFactory.getConnection()) {
                        String ping = connection.ping();
                        statusMap.put(ServiceConstants.REDIS_CLICK, "PONG".equalsIgnoreCase(ping) ? "UP" : "DOWN");
                    } catch (Exception e) {
                        statusMap.put(ServiceConstants.REDIS_CLICK, "DOWN");
                    }
                    break;

                case ServiceConstants.MONGO:
                    try {
                        mongoTemplate.executeCommand("{ ping: 1 }");
                        statusMap.put(ServiceConstants.MONGO, "UP");
                    } catch (Exception e) {
                        statusMap.put(ServiceConstants.MONGO, "DOWN");
                    }
                    break;
                case ServiceConstants.POSTGRES:
                    try {
                        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                        statusMap.put(ServiceConstants.POSTGRES, (result != null && result == 1) ? "UP" : "DOWN");
                    } catch (Exception e) {
                        statusMap.put(ServiceConstants.POSTGRES, "DOWN");
                    }
                    break;
                default:
                    statusMap.put(s.trim(), "UNKNOWN SERVICE");
            }
        }

        return statusMap;
    }
}
