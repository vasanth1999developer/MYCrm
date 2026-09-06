package com.mycrm.aiservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds to the `anthropic.*` block in application.yml.
 * Keep the API key out of source control — inject it via env var (see application.yml).
 */
@ConfigurationProperties(prefix = "anthropic")
public class AnthropicProperties {

    private String apiKey;
    private String baseUrl = "https://api.anthropic.com";
    private String model = "claude-sonnet-4-6";
    private String apiVersion = "2023-06-01";
    private int maxTokens = 1024;

    // getters and setters

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}
