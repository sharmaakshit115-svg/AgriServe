package com.agriserve.service;

import com.agriserve.entity.AdvisoryContent;
import com.agriserve.repository.AdvisoryContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisoryContentServiceImpl implements AdvisoryContentService {

    @Autowired
    private AdvisoryContentRepository repo;

    @Override
    public AdvisoryContent createContent(AdvisoryContent content) {
        return repo.save(content);
    }

    @Override
    public List<AdvisoryContent> getAll() {
        return repo.findAll();
    }

    @Override
    public AdvisoryContent getById(Long id) {
        return repo.findById(id).orElseThrow();
    }
}