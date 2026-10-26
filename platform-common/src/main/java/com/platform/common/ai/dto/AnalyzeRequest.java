package com.platform.common.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzeRequest {
    /** single goods */
    private GoodsSnapshot goods;
    /** optional batch */
    private List<GoodsSnapshot> goodsList;
}
