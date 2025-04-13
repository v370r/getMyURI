package com.getmyuri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.model.ClickMetric;
import com.getmyuri.model.DataObjectFormat;
import com.getmyuri.repository.ClickMetricRepository;
import com.getmyuri.service.LinkService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/links")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private ClickMetricRepository clickMetricRepository;

    @PostMapping
    public ResponseEntity<DataObjectFormat> createLink(@RequestBody LinkDTO linkDTO) {
        DataObjectFormat savedLink = linkService.createLink(linkDTO);
        return ResponseEntity.ok(savedLink);
    }

    @GetMapping
    public ResponseEntity<List<DataObjectFormat>> getAllLinks() {
        List<DataObjectFormat> links = linkService.getAllLinks();
        return ResponseEntity.ok(links);
    }

    @DeleteMapping("/{id}") // Decomissioned
    public ResponseEntity<String> deleteLink(@PathVariable String id) {
        boolean deleted = linkService.deleteLink(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted link with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/**")
    public ResponseEntity<String> deleteLinkByAlias(HttpServletRequest request) {
        String aliasPath = request.getRequestURI().replaceFirst("/api/links/", "");
        boolean deleted = linkService.deleteLinkByAliasPath(aliasPath);
        if (deleted) {
            return ResponseEntity.ok("Deleted alias: " + aliasPath);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" Alias not found: " + aliasPath);
        }
    }

    @GetMapping("/click-stats")
    public ResponseEntity<List<ClickMetric>> getClickStats(@RequestParam String username) {
        List<ClickMetric> stats = clickMetricRepository.findByUsernameOrderByClickDateDesc(username);
        return ResponseEntity.ok(stats);
    }

}
