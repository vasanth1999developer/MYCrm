package com.mycrm.aiservice.client;

import com.mycrm.aiservice.config.AnthropicProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Anthropic /v1/messages endpoint.
 * Keep this class dumb — it only knows how to talk to the API.
 * All prompt-building / business logic belongs in the service layer.
 */
@Component
public class AnthropicClient {

    private final WebClient webClient;
    private final AnthropicProperties props;

    public AnthropicClient(WebClient anthropicWebClient, AnthropicProperties props) {
        this.webClient = anthropicWebClient;
        this.props = props;
    }

    public Mono<String> complete(String systemPrompt, String userMessage) {
        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "max_tokens", props.getMaxTokens(),
                "system", systemPrompt,
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                )
        );

        return webClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::extractText);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content == null || content.isEmpty()) {
            return "";
        }
        // Concatenate all text blocks; ignore tool_use blocks for this simple client
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> block : content) {
            if ("text".equals(block.get("type"))) {
                sb.append(block.get("text"));
            }
        }
        return sb.toString();
    }
}
