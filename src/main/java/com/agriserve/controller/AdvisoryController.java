package com.agriserve.controller;

import com.agriserve.entity.AdvisoryContent;
import com.agriserve.service.AdvisoryContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

    @Autowired
    private AdvisoryContentService contentService;

    @PostMapping("/content")
    public AdvisoryContent createContent(@RequestBody AdvisoryContent content) {
        return contentService.createContent(content);
    }

    @GetMapping("/content")
    public List<AdvisoryContent> listAll() {
        return contentService.getAll();
    }
}