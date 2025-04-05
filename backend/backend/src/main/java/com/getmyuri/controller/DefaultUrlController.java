package com.getmyuri.controller;

import java.util.Map;

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

    @Autowired
    private DefaultUrlService defaultUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody LinkDTO linkDTO) {
        String longUrl = linkDTO.getLink();

        if (longUrl == null || longUrl.isBlank()) {
            return ResponseEntity.badRequest().body("Missing longUrl");
        }

        String shortUrl = defaultUrlService.createShortUrl(linkDTO);
        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }
}
