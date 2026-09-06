package com.mycrm.aiservice.client;

import com.mycrm.aiservice.config.ClaudeProperties;
import com.mycrm.aiservice.exception.AiServiceException;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

/**
 * Thin wrapper around the Anthropic Messages API.
 * All outbound AI calls in ai-service should go through this single client so
 * that retries, timeouts, and logging are handled in one place.
 */
@Component
public class ClaudeClient {

    private final WebClient webClient;
    private final ClaudeProperties props;

    public ClaudeClient(WebClient claudeWebClient, ClaudeProperties props) {
        this.webClient = claudeWebClient;
        this.props = props;
    }

    @Retry(name = "claudeApi")
    public String sendPrompt(String systemContext, String userPrompt) {
        String combinedPrompt = systemContext == null || systemContext.isBlank()
                ? userPrompt
                : systemContext + "\n\n" + userPrompt;

        ClaudeMessageRequest request = new ClaudeMessageRequest(
                props.getModel(),
                props.getMaxTokens(),
                List.of(new ClaudeMessageRequest.Message("user", combinedPrompt))
        );

        try {
            ClaudeMessageResponse response = webClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ClaudeMessageResponse.class)
                    .block();

            if (response == null) {
                throw new AiServiceException("Empty response from Claude API");
            }
            return response.extractText();

        } catch (WebClientResponseException e) {
            throw new AiServiceException(
                    "Claude API call failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }
}
