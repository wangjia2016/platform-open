package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Query
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
@ApiModel("订单支付参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderPayPortalRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户openid
	 */
	@ApiModelProperty("用户openid")
	private String openid;

	@ApiModelProperty("订单id")
	private Long orderId;

	@ApiModelProperty("订单号")
	private Long orderNo;

	@ApiModelProperty("支付类型 微信支付：wechat 支付宝：alipay")
	private String payType;

	@ApiModelProperty("总计金额")
	private BigDecimal totalAmount;

	@ApiModelProperty("商品描述")
	private String goodsName;

	@ApiModelProperty("交易类型 JSAPI:JSAPI支付 NATIVE:Native支付 APP:APP支付")
	private String tradeType;

	@ApiModelProperty("总计金额")
	private String appId;

}
