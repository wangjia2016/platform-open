package com.platform.common.ai.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SearchPlanRequest {
    private String query;
    private String locale;
    private Map<String, Object> hints;
}
