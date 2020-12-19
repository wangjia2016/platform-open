package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ClassName: MallCartRequestQuery.java
 * @Description: 购物车封装
 * @Author: 王佳
 * @Date: 2020年11月6日
 * @Version:1.0
 * @Copyright:©
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("订单项参数")
public class MallCartRequestQuery extends MallCartQuery {

    @ApiModelProperty("商品编号")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("数量")
    private Integer number;

    private Long credit;

    private BigDecimal price;

    @ApiModelProperty("商品logo")
	private String logoUrl;

    @ApiModelProperty("商品类型")
    private Integer goodsType;
}
