package com.platform.mall.dao.order.model.query;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单物流信息Query
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@ApiModel("订单物流信息Query")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderDeliveryQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@ApiModelProperty("")
	private Long id;

	/**
	 * 订单编号
	 */
	@ApiModelProperty("订单编号")
	private Long orderNo;

	/**
	 * 订单id
	 */
	private Long orderId;

	/**
	 * 会员编号
	 */
	@ApiModelProperty("会员编号")
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
	 * 物流公司名称
	 */
	@ApiModelProperty("物流公司名称")
	private String deliveryCompanyName;

	/**
	 * 物流编号
	 */
	@ApiModelProperty("物流编号")
	private String deliveryNo;

	/**
	 * 发货时间
	 */
	@ApiModelProperty("发货时间")
	private Date deliveryTime;

	/**
	 * 省
	 */
	@ApiModelProperty("省")
	private String province;

	/**
	 * 市
	 */
	@ApiModelProperty("市")
	private String city;

	/**
	 * 区
	 */
	@ApiModelProperty("区")
	private String area;

	/**
	 * 送货地址
	 */
	@ApiModelProperty("送货地址")
	private String address;



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
