package com.mycrm.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SummarizeLeadRequest(
        @NotNull Long leadId,
        @NotBlank String leadName,
        List<ActivityEntry> activities
) {
    public record ActivityEntry(String type, String note, String timestamp) {}
}
