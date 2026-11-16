package com.mycrm.aiservice.client;

import java.util.List;

/** Request body shape for POST /v1/messages. */
public record ClaudeMessageRequest(
        String model,
        int max_tokens,
        List<Message> messages
) {
    public record Message(String role, String content) {}
}
