package com.getmyuri.controller;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.getmyuri.dto.ResolvedLinkDTO;
import com.getmyuri.service.GeoUtils;
import com.getmyuri.service.LinkService;
import com.getmyuri.service.redis.RedisClickService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/r")
public class RedirectController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private RedisClickService redisClickService;

    @GetMapping("/**")
    public ResponseEntity<?> catchRedirect(HttpServletRequest request,
            @RequestParam(required = false) String passcode,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        String fullPath = request.getRequestURI().replaceFirst("/r/", "");
        logger.info("Received aliasPath: {}", fullPath);
    
        // 1) Fetch the link DTO
        Optional<ResolvedLinkDTO> resolvedUrl = linkService.getLink(fullPath);
        if (resolvedUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid alias or passcode"));
        }
        ResolvedLinkDTO dto = resolvedUrl.get();
    
        // 2) Determine which checks are required
        boolean locationRequired = dto.getLocation() != null && dto.getRadius() != null;
        boolean passwordRequired = dto.getPassword() != null && !dto.getPassword().isEmpty();
    
        // 3) If either credential is missing, redirect to auth endpoint in one go
        if ((locationRequired && (lat == null || lon == null)) ||
            (passwordRequired && passcode == null)) {
    
            String redirectUrl = UriComponentsBuilder
                    .fromUriString("http://app.getmyuri.com/auth")
                    .queryParam("aliasPath", fullPath)
                    .queryParam("location_required", locationRequired)
                    .queryParam("password_required", passwordRequired)
                    .build()
                    .toUriString();
    
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        }
    
        // 4) Now that both (if required) are present, verify them:
    
        // 4a) Location check
        if (locationRequired) {
            boolean within = GeoUtils.isWithinRadius(
                dto.getLocation(), lat, lon, dto.getRadius());
            if (!within) {
                logger.warn("Access denied for alias {}: user is outside allowed radius", fullPath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "error", "Access denied",
                            "reason", "You are outside the allowed radius",
                            "location_required", true));
            }
        }
    
        // 4b) Password check
        if (passwordRequired && !dto.getPassword().equals(passcode)) {
            logger.info("Password incorrect for alias: {}", fullPath);
            String redirectUrl = UriComponentsBuilder
                    .fromUriString("http://app.getmyuri.com/auth")
                    .queryParam("aliasPath", fullPath)
                    .queryParam("location_required", locationRequired)
                    .queryParam("password_required", passwordRequired)
                    .build()
                    .toUriString();
    
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        }
    
        // 5) All checks passed → record click and redirect
        redisClickService.incrementClick(dto.getUsername(), dto.getAlias());
        logger.info("Redirecting to target URL for alias: {}", fullPath);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(dto.getLink()))
                .build();
    }
    
    @GetMapping("/ping")
    public String ping() {
        logger.debug("Ping endpoint hit for RedirectController");
        return " RedirectController active";
    }

}
