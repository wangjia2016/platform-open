package com.platform.ai.llm;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {
    public String load(String classpathName) {
        try {
            ClassPathResource res = new ClassPathResource("prompts/" + classpathName);
            if (!res.exists()) {
                return "";
            }
            return StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
