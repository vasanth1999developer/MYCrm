package com.clinch.crm.ai.controller;

import com.clinch.crm.ai.dto.SummarizeLeadRequest;
import com.clinch.crm.ai.dto.SummarizeLeadResponse;
import com.clinch.crm.ai.service.LeadSummarizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final LeadSummarizationService summarizationService;

    public AiController(LeadSummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }

    @PostMapping("/summarize-lead")
    public SummarizeLeadResponse summarizeLead(@Valid @RequestBody SummarizeLeadRequest request) {
        return summarizationService.summarize(request);
    }
}
