package com.platform.common.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsSnapshot {
    private Long id;
    private String name;
    private String title;
    private String code;
    private Long typeId;
    private Long brandId;
    private Integer stock;
    private Integer stockWarn;
    private Integer auditStatus;
    /** 0 on sale, 1 off shelf */
    private Integer onUnSale;
    private BigDecimal proposedPrice;
    private BigDecimal price;
}
