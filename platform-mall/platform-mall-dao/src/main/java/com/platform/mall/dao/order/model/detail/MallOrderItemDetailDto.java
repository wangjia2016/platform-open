package com.platform.mall.dao.order.model.detail;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 订单项，订单包含的商品、订单详情DetailDto
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@ApiModel("订单项，订单包含的商品、订单详情Detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderItemDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using  = ToStringSerializer.class)
	@ApiModelProperty("")
	private Long id;

	/**
	 * 订单id
	 */
	@ApiModelProperty("订单id")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long orderId;

	/**
	 * 订单编号
	 */
	@ApiModelProperty("订单编号")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long orderNo;

	/**
	 * 会员编号
	 */
	@ApiModelProperty("会员编号")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long memberId;

	/**
	 * 用户openid
	 */
	@ApiModelProperty("用户openid")
	private String openid;

	/**
	 * app_id
	 */
	@ApiModelProperty("app_id")
	private String appId;

	/**
	 * union_id
	 */
	@ApiModelProperty("union_id")
	private String unionId;

	/**
	 * 商品编号
	 */
	@ApiModelProperty("商品编号")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long goodsId;

	/**
	 * 数量
	 */
	@ApiModelProperty("数量")
	private Integer number;

	/**
	 * 商品成本价
	 */
	@ApiModelProperty("商品成本价")
	private BigDecimal costPrice;

	/**
	 * 价格
	 */
	@ApiModelProperty("价格")
	private BigDecimal price;

	/**
	 * 储值支付金额
	 */
	@ApiModelProperty("储值支付金额")
	private BigDecimal rechargeBalance;

	/**
	 * 优惠价格
	 */
	@ApiModelProperty("优惠价格")
	private BigDecimal concessionaryPrice;

	/**
	 * 商品名称
	 */
	@ApiModelProperty("商品名称")
	private String goodsName;

	/**
	 * 商品logo
	 */
	@ApiModelProperty("商品logo")
	private String goodsLogo;

	/**
	 * sku_id
	 */
	@ApiModelProperty("sku_id")
	@JsonSerialize(using  = ToStringSerializer.class)
	private Long skuId;

	/**
	 * sku_attr属性
	 */
	@ApiModelProperty("sku_attr属性")
	private String skuAttr;

	/**
	 * 所属分销商
	 */
	@ApiModelProperty("所属分销商")
	private Long distributorId;

	/**
	 * 0 正常 1 删除
	 */
	@ApiModelProperty("0 正常 1 删除")
	private Integer isDelete;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@ApiModelProperty("修改时间")
	private Date updateDatetime;

	/**
	 * 租户id
	 */
	@ApiModelProperty("租户id")
	private String tenantId;
}
