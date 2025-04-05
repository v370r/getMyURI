package com.getmyuri.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexConfig implements InitializingBean {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() {
        Index index = new Index()
                .on("expiresAt", Sort.Direction.ASC)
                .expire(0, TimeUnit.SECONDS); // delete exactly at expiresAt
        mongoTemplate.indexOps("links").ensureIndex(index);
    }
}
