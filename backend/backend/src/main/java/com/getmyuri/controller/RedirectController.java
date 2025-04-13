package com.getmyuri.controller;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

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
        System.out.println("Received aliasPath: " + fullPath);

        String[] parts = fullPath.split("/");
        if (parts.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid request format"));
        }

        Optional<ResolvedLinkDTO> resolvedUrl = linkService.getLink(fullPath);

        if (resolvedUrl.isPresent()) {
            ResolvedLinkDTO dto = resolvedUrl.get();
            if (dto.getLocation() != null && dto.getRadius() != null) {
                if (lat == null && lon == null) {
                    String redirectUrl = UriComponentsBuilder.fromUriString("https://getmyuri.com/auth")
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
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of(
                                        "error", "Access denied",
                                        "reason", "You are outside the allowed radius",
                                        "location_required", true));
                    }
                }

            }
            if (dto.getPassword().length() != 0 && dto.getPassword() != null && !dto.getPassword().equals(passcode)) {

                String redirectUrl = UriComponentsBuilder.fromUriString("https://getmyuri.com/auth")
                        .queryParam("aliasPath", fullPath)
                        .queryParam("link", dto.getLink())
                        .queryParam("location_required", dto.getLocation() != null)
                        .queryParam("password_required", dto.getPassword() != null)
                        .build().toUriString();

                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl)) // This sends the 302 redirect with Location header
                        .build();

            }
            redisClickService.incrementClick(dto.getUsername(), dto.getAlias());
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(dto.getLink())).build();
        } else

        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid alias or passcode"));
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return " RedirectController active";
    }

}
