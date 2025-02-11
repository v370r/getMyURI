package com.getmyuri.backend;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class BackendApplication {

	private final Map<String, String> urlMappings = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostMapping("/add-route")
	public String addRoute(@RequestParam String path, @RequestParam String targetUrl) {
		urlMappings.put(path, targetUrl);
		return "Route added: " + path + " -> " + targetUrl;
	}

	@GetMapping("/{path}")
	public ResponseEntity<Void> redirect(@PathVariable String path) {
		String targetUrl = urlMappings.get(path);
		if (targetUrl != null) {
			return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create(targetUrl)).build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
