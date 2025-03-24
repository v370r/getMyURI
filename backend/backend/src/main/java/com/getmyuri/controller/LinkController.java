package com.getmyuri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.model.DataObjectFormat;
import com.getmyuri.service.LinkService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/links")
public class LinkController {

    @Autowired
    private LinkService linkService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLink(@PathVariable String id) {
        boolean deleted = linkService.deleteLink(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted link with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
