package com.platform.ai.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.config.AiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleClient.class);

    private final RestTemplate restTemplate;
    private final AiProperties props;
    private final ObjectMapper objectMapper;

    public OpenAiCompatibleClient(RestTemplate aiRestTemplate, AiProperties props, ObjectMapper objectMapper) {
        this.restTemplate = aiRestTemplate;
        this.props = props;
        this.objectMapper = objectMapper;
    }

    public String chatJson(String systemPrompt, String userPayload) {
        if (!props.isLlmConfigured()) {
            throw new IllegalStateException("AI_NOT_CONFIGURED");
        }
        String url = trimSlash(props.getBaseUrl()) + "/chat/completions";
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("model", props.getModel());
        body.put("temperature", 0.2);
        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        Map<String, String> sys = new HashMap<String, String>();
        sys.put("role", "system");
        sys.put("content", systemPrompt);
        Map<String, String> user = new HashMap<String, String>();
        user.put("role", "user");
        user.put("content", userPayload);
        messages.add(sys);
        messages.add(user);
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getApiKey());
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(url, new HttpEntity<Object>(body, headers), String.class);
            JsonNode root = objectMapper.readTree(resp.getBody());
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().trim().isEmpty()) {
                throw new IllegalStateException("AI_BAD_OUTPUT");
            }
            return stripFence(content.asText());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.warn("LLM call failed: {}", e.toString());
            throw new IllegalStateException("AI_TIMEOUT", e);
        }
    }

    private static String trimSlash(String base) {
        if (base == null || base.isEmpty()) {
            return "https://api.openai.com/v1";
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    static String stripFence(String text) {
        String t = text.trim();
        if (t.startsWith("```")) {
            int nl = t.indexOf('\n');
            if (nl > 0) {
                t = t.substring(nl + 1);
            }
            if (t.endsWith("```")) {
                t = t.substring(0, t.length() - 3);
            }
        }
        return t.trim();
    }
}
