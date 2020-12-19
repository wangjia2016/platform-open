package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车Query
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@ApiModel("购物车Query")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallCartQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@ApiModelProperty("")
	private Long id;

	/**
	 * 用户编号
	 */
	@ApiModelProperty("用户编号")
	private Long memberId;

	/**
	 * 商品编号
	 */
	@ApiModelProperty("商品编号")
	private Long goodsId;

	/**
	 * 数量
	 */
	@ApiModelProperty("数量")
	private Integer number;

	/**
	 * 
	 */
	@ApiModelProperty("")
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
	 * 所属分销商
	 */
	@ApiModelProperty("所属分销商")
	private Long distributorId;

	/**
	 * 1 物流配送 2到店自提
	 */
	@ApiModelProperty("1 物流配送 2到店自提")
	private String deliveryType;

	/**
	 * sku_id
	 */
	@ApiModelProperty("sku_id")
	private Long skuId;

	/**
	 * sku_attr属性
	 */
	@ApiModelProperty("sku_attr属性")
	private String skuAttr;

	/**
	 * T true  F false
	 */
	@ApiModelProperty("T true  F false")
	private String isDelete;

	/**
	 * 创建人
	 */
	@ApiModelProperty("创建人")
	private String createUser;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 修改人
	 */
	@ApiModelProperty("修改人")
	private String updateUser;

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
