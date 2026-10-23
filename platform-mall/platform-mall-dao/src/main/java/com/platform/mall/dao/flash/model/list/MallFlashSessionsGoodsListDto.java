package com.platform.mall.dao.flash.model.list;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("秒杀场次关联商品List")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsGoodsListDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("关联记录id")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("场次id")
    private Long sessionsId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("商品编号")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String goodsTitle;

    @ApiModelProperty("商品logo")
    private String goodsLogo;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("秒杀价")
    private BigDecimal flashPrice;

    @ApiModelProperty("场次库存")
    private Integer flashStock;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("0上架 1下架")
    private Integer onUnSale;

    @ApiModelProperty("排序")
    private Integer listOrder;
}
