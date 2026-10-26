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
public class SearchPlanResponse {
    private String intent;
    @Builder.Default
    private List<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();
    @Builder.Default
    private List<Map<String, Object>> sort = new ArrayList<Map<String, Object>>();
    @Builder.Default
    private List<String> keywords = new ArrayList<String>();
    private String pseudoSql;
    @Builder.Default
    private List<String> notes = new ArrayList<String>();
    private boolean degraded;
    private String strategy;
}
