package com.platform.mall.dao.flash.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("秒杀场次关联商品参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsGoodsBindQuery {

    @ApiModelProperty(value = "场次id", required = true)
    private Long sessionsId;

    @ApiModelProperty(value = "商品编号", required = true)
    private Long goodsId;

    @ApiModelProperty("秒杀价")
    private BigDecimal flashPrice;

    @ApiModelProperty("场次库存")
    private Integer flashStock;

    @ApiModelProperty("排序")
    private Integer listOrder;

    @ApiModelProperty("租户id")
    private String tenantId;
}
