package com.agriserve.controller;

import com.agriserve.dto.FeedbackDTO;
import com.agriserve.entity.Feedback;
import com.agriserve.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackDTO dto) {
        return ResponseEntity.ok(feedbackService.submitFeedback(dto));
    }


    @GetMapping("/officer/{officerId}/list")
    public ResponseEntity<List<Feedback>> getOfficerFeedback(@PathVariable Long officerId) {
        return ResponseEntity.ok(feedbackService.getFeedbackForOfficer(officerId));
    }


    @GetMapping("/officer/{officerId}/average")
    public ResponseEntity<Double> getAvg(@PathVariable Long officerId) {
        return ResponseEntity.ok(feedbackService.getOfficerPerformance(officerId));
    }
}