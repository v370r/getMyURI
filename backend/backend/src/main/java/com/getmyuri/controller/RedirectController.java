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
        logger.info("Received redirect request for aliasPath: {}", fullPath);

        String[] parts = fullPath.split("/");
        if (parts.length == 0) {
            logger.warn("Invalid redirect request format: {}", fullPath);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid request format"));
        }

        Optional<ResolvedLinkDTO> resolvedUrl = linkService.getLink(fullPath);

        if (resolvedUrl.isPresent()) {
            ResolvedLinkDTO dto = resolvedUrl.get();
            logger.debug("Resolved URL: {}", dto.getLink());
            if (dto.getLocation() != null && dto.getRadius() != null) {
                if (lat == null && lon == null) {
                    logger.info("Location required but not provided for alias: {}", fullPath);
                    String redirectUrl = UriComponentsBuilder.fromUriString("https://app.getmyuri.com/auth")
                            .queryParam("aliasPath", fullPath)
                            .queryParam("location_required", true)
                            .queryParam("password_required", dto.getPassword() != null)
                            .build().toUriString();

                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(redirectUrl))
                            .build();
                } else {
                    boolean iswithIn = GeoUtils.isWithinRadius(dto.getLocation(), lat, lon, dto.getRadius());
                    if (!iswithIn) {
                        logger.warn("Access denied for alias {}: user is outside allowed radius", fullPath);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of(
                                        "error", "Access denied",
                                        "reason", "You are outside the allowed radius",
                                        "location_required", true));
                    }
                }

            }
            if (dto.getPassword().length() != 0 && dto.getPassword() != null && !dto.getPassword().equals(passcode)) {

                logger.info("Password required or incorrect for alias: {}", fullPath);
                String redirectUrl = UriComponentsBuilder.fromUriString("https:/app.getmyuri.com/auth")
                        .queryParam("aliasPath", fullPath)
                        .queryParam("location_required", dto.getLocation() != null)
                        .queryParam("password_required", dto.getPassword() != null)
                        .build().toUriString();

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();

            }
            redisClickService.incrementClick(dto.getUsername(), dto.getAlias());
            logger.info("Redirecting to target URL for alias: {}", fullPath);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(dto.getLink())).build();
        } else {
            logger.warn("Invalid alias or passcode for redirect request: {}", fullPath);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid alias or passcode"));
        }
    }

    @GetMapping("/ping")
    public String ping() {
        logger.debug("Ping endpoint hit for RedirectController");
        return " RedirectController active";
    }

}
