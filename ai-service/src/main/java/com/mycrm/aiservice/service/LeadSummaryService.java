package com.mycrm.aiservice.service;

import com.mycrm.aiservice.client.AnthropicClient;
import com.mycrm.aiservice.dto.SummarizeLeadRequest;
import com.mycrm.aiservice.dto.SummarizeLeadResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LeadSummaryService {

    // Treat this as versioned config later (e.g. load from a prompts/ resource or a
    // prompt-management table) rather than hardcoding — makes iteration much easier.
    private static final String SYSTEM_PROMPT = """
            You are a sales assistant summarizing CRM lead activity for a busy sales rep.
            Given a raw activity history (notes, emails, call logs), produce:
            1. A 2-3 sentence summary of where this lead stands.
            2. The single most important next action.
            Be concise and factual. Do not invent details not present in the activity history.
            """;

    private final AnthropicClient anthropicClient;

    public LeadSummaryService(AnthropicClient anthropicClient) {
        this.anthropicClient = anthropicClient;
    }

    public Mono<SummarizeLeadResponse> summarize(SummarizeLeadRequest request) {
        return anthropicClient
                .complete(SYSTEM_PROMPT, request.activityHistory())
                .map(summary -> new SummarizeLeadResponse(request.leadId(), summary));
    }
}
