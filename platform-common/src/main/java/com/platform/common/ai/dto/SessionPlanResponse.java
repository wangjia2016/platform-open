package com.platform.common.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionPlanResponse {
    @Builder.Default
    private List<Map<String, Object>> sessions = new ArrayList<Map<String, Object>>();
    @Builder.Default
    private List<Map<String, Object>> bindSuggestions = new ArrayList<Map<String, Object>>();
    @Builder.Default
    private List<String> riskNotes = new ArrayList<String>();
    private boolean degraded;
    private String strategy;
}
