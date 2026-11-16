package com.clinch.crm.ai.dto;

public record SummarizeLeadResponse(
        Long leadId,
        String summary,
        String model
) {}
