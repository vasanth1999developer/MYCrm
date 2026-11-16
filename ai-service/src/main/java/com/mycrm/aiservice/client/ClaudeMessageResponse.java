package com.mycrm.aiservice.client;

import java.util.List;

/** Response body shape from POST /v1/messages. */
public record ClaudeMessageResponse(
        String id,
        String model,
        List<ContentBlock> content,
        Usage usage
) {
    public record ContentBlock(String type, String text) {}
    public record Usage(int input_tokens, int output_tokens) {}

    /** Convenience accessor: concatenates all text blocks in the response. */
    public String extractText() {
        if (content == null) return "";
        StringBuilder sb = new StringBuilder();
        for (ContentBlock block : content) {
            if ("text".equals(block.type())) {
                sb.append(block.text());
            }
        }
        return sb.toString();
    }
}
