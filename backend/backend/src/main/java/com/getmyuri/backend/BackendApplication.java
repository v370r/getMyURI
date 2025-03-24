package com.getmyuri.backend;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.getmyuri")
@EnableJpaRepositories("com.getmyuri.repository")
@EnableMongoRepositories("com.getmyuri.repository")
@EntityScan("com.getmyuri.model")
@EnableScheduling
public class BackendApplication {

	private final Map<String, String> urlMappings = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// @PostMapping("/add-route")
	// public String addRoute(@RequestParam String path, @RequestParam String
	// targetUrl) {
	// urlMappings.put(path, targetUrl);
	// return "Route added: " + path + " -> " + targetUrl;
	// }

	// @GetMapping("/{path}")
	// public ResponseEntity<Void> redirect(@PathVariable String path) {
	// String targetUrl = urlMappings.get(path);
	// if (targetUrl != null) {
	// System.out.println("Routing to: " + path + " -> " + targetUrl);
	// return
	// ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create(targetUrl)).build();
	// } else {
	// return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	// }
	// }
}
