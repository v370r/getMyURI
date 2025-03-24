package com.getmyuri.controller;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.getmyuri.dto.ResolvedLinkDTO;
import com.getmyuri.service.LinkService;
import com.getmyuri.service.RedisClickService;

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
            @RequestParam(required = false) String passcode) {
        String fullPath = request.getRequestURI().replaceFirst("/r/", "");
        System.out.println("Received aliasPath: " + fullPath);

        Optional<ResolvedLinkDTO> resolvedUrl = linkService.getLinkIfPasswordMatches(fullPath, passcode);

        if (resolvedUrl.isPresent()) {
            ResolvedLinkDTO dto = resolvedUrl.get();
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
