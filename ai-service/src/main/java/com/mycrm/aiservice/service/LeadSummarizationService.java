package com.mycrm.aiservice.service;

import com.mycrm.aiservice.client.ClaudeClient;
import com.mycrm.aiservice.config.ClaudeProperties;
import com.mycrm.aiservice.dto.SummarizeLeadRequest;
import com.mycrm.aiservice.dto.SummarizeLeadResponse;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LeadSummarizationService {

    private static final String SYSTEM_CONTEXT = """
            You are a CRM assistant. Summarize a sales lead's activity history in
            3-5 concise bullet points a busy sales rep can scan in seconds.
            Focus on: engagement trend, buying signals, objections, and a
            suggested next action. Do not invent facts not present in the
            activity log.
            """;

    private final ClaudeClient claudeClient;
    private final ClaudeProperties claudeProperties;

    public LeadSummarizationService(ClaudeClient claudeClient, ClaudeProperties claudeProperties) {
        this.claudeClient = claudeClient;
        this.claudeProperties = claudeProperties;
    }

    public SummarizeLeadResponse summarize(SummarizeLeadRequest request) {
        String activityLog = request.activities() == null || request.activities().isEmpty()
                ? "No recorded activity."
                : request.activities().stream()
                    .map(a -> "- [%s] (%s): %s".formatted(a.type(), a.timestamp(), a.note()))
                    .collect(Collectors.joining("\n"));

        String userPrompt = """
                Lead: %s (ID: %d)

                Activity history:
                %s
                """.formatted(request.leadName(), request.leadId(), activityLog);

        String summaryText = claudeClient.sendPrompt(SYSTEM_CONTEXT, userPrompt);

        return new SummarizeLeadResponse(request.leadId(), summaryText, claudeProperties.getModel());
    }
}
