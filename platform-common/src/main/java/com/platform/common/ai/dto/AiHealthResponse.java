package com.platform.common.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHealthResponse {
    private String status;
    private boolean aiEnabled;
    private boolean llmConfigured;
    private String model;
}
