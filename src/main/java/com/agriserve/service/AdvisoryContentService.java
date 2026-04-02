package com.agriserve.service;

import com.agriserve.entity.AdvisoryContent;

import java.util.List;

public interface AdvisoryContentService {
    AdvisoryContent createContent(AdvisoryContent content);
    List<AdvisoryContent> getAll();
    AdvisoryContent getById(Long id);
}