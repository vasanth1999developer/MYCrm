package com.mycrm.aiservice.dto;

public record SummarizeLeadResponse(
        Long leadId,
        String summary,
        String model
) {}
