package com.platform.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform.ai")
public class AiProperties {
    /** Master switch; when false always use rule fallback. */
    private boolean enabled = false;
    private String apiKey = "";
    private String baseUrl = "https://api.openai.com/v1";
    private String model = "gpt-4o-mini";
    private int timeoutMs = 8000;
    /** Per-capability LLM gates (only matter when isLlmConfigured()). */
    private Features features = new Features();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    public Features getFeatures() { return features; }
    public void setFeatures(Features features) { this.features = features == null ? new Features() : features; }

    public boolean isLlmConfigured() {
        return enabled && apiKey != null && apiKey.trim().length() > 0
                && !"change-me".equalsIgnoreCase(apiKey.trim());
    }

    /** Whether this capability may call the model (master + key + feature flag). */
    public boolean llmAllowed(String capability) {
        if (!isLlmConfigured()) {
            return false;
        }
        if (capability == null) {
            return true;
        }
        switch (capability) {
            case "search-plan":
                return features.isSearchPlan();
            case "recommend":
                return features.isRecommend();
            case "analyze":
                return features.isAnalyze();
            case "session-plan":
                return features.isSessionPlan();
            default:
                return true;
        }
    }

    public static class Features {
        private boolean searchPlan = true;
        private boolean recommend = true;
        private boolean analyze = true;
        private boolean sessionPlan = true;

        public boolean isSearchPlan() { return searchPlan; }
        public void setSearchPlan(boolean searchPlan) { this.searchPlan = searchPlan; }
        public boolean isRecommend() { return recommend; }
        public void setRecommend(boolean recommend) { this.recommend = recommend; }
        public boolean isAnalyze() { return analyze; }
        public void setAnalyze(boolean analyze) { this.analyze = analyze; }
        public boolean isSessionPlan() { return sessionPlan; }
        public void setSessionPlan(boolean sessionPlan) { this.sessionPlan = sessionPlan; }
    }
}
