package com.mycrm.aiservice;

import com.mycrm.aiservice.client.ClaudeClient;
import com.mycrm.aiservice.config.ClaudeProperties;
import com.mycrm.aiservice.dto.SummarizeLeadRequest;
import com.mycrm.aiservice.dto.SummarizeLeadResponse;
import com.mycrm.aiservice.service.LeadSummarizationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class LeadSummarizationServiceTest {

    @Test
    void summarize_buildsPromptAndReturnsClaudeText() {
        ClaudeClient claudeClient = mock(ClaudeClient.class);
        ClaudeProperties props = new ClaudeProperties();
        props.setModel("claude-sonnet-4-6");

        when(claudeClient.sendPrompt(anyString(), anyString()))
                .thenReturn("- Engaged twice this week\n- Ready for pricing call");

        LeadSummarizationService service = new LeadSummarizationService(claudeClient, props);

        SummarizeLeadRequest request = new SummarizeLeadRequest(
                42L,
                "Acme Corp",
                List.of(new SummarizeLeadRequest.ActivityEntry("CALL", "Discussed pricing", "2026-07-20T10:00:00Z"))
        );

        SummarizeLeadResponse response = service.summarize(request);

        assertEquals(42L, response.leadId());
        assertEquals("claude-sonnet-4-6", response.model());
        assertEquals("- Engaged twice this week\n- Ready for pricing call", response.summary());
    }
}
