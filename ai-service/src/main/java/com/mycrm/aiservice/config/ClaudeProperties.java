package com.mycrm.aiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the `claude.*` properties from application.yml.
 * Keep the API key out of source control — it's read from the
 * ANTHROPIC_API_KEY environment variable at runtime.
 */
@ConfigurationProperties(prefix = "claude")
public class ClaudeProperties {

    private String apiKey;
    private String baseUrl;
    private String model;
    private int maxTokens;
    private int timeoutSeconds;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
}
