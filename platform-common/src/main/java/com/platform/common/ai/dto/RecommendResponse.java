package com.platform.common.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponse {
    @Builder.Default
    private List<RecommendItem> items = new ArrayList<RecommendItem>();
    private String strategy;
    private boolean degraded;
    @Builder.Default
    private List<String> notes = new ArrayList<String>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendItem {
        private Long goodsId;
        private double score;
        private List<String> reasons;
    }
}
