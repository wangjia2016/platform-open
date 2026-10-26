package com.platform.common.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeResponse {
    private String summary;
    @Builder.Default
    private List<String> sellingPoints = new ArrayList<String>();
    @Builder.Default
    private List<String> risks = new ArrayList<String>();
    @Builder.Default
    private Map<String, String> copyDrafts = new HashMap<String, String>();
    private String pricingNotes;
    private boolean degraded;
    private String strategy;
}
