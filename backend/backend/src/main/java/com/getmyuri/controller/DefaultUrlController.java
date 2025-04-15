package com.getmyuri.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.service.DefaultUrlService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/default")
public class DefaultUrlController {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUrlController.class);

    @Autowired
    private DefaultUrlService defaultUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody LinkDTO linkDTO) {
        String longUrl = linkDTO.getLink();
        logger.info("Received request to shorten URL: {}", longUrl);

        if (longUrl == null || longUrl.isBlank()) {
            logger.warn("Missing longUrl in request");
            return ResponseEntity.badRequest().body("Missing longUrl");
        }

        String shortUrl = defaultUrlService.createShortUrl(linkDTO);
        logger.info("Generated short URL: {}", shortUrl);
        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }
}
