package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: MallOrderRequestQuery.java
 * @Description: 普通订单保存类型封装
 * @Author: 王佳
 * @Date: 2020年11月6日
 * @Version:1.0
 * @Copyright:©
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("普通订单提交参数")
public class MallOrderRequestQuery  {

	@ApiModelProperty("会员编号")
	private Long memberId;

//	@ApiModelProperty("订单id-前端不需要传")
//	private Long id;

	@ApiModelProperty("订单编号-前端不用传")
	private Long orderNo;

	@ApiModelProperty("用户openid")
	private String openid;

	@ApiModelProperty("1 物流配送 2到店自提")
	private Integer deliveryType;

	@ApiModelProperty("联系人")
	private String receiver;

	@ApiModelProperty("联系方式")
	private String contact;

	@ApiModelProperty("省")
	private String province;

	@ApiModelProperty("市")
	private String city;

	@ApiModelProperty("区")
	private String area;

	@ApiModelProperty("详细地址")
	private String address;

	@ApiModelProperty("配送点名称")
	private String locationName;

	@ApiModelProperty("配送点联系人")
	private String locationReceiver;

	@ApiModelProperty("配送点联系方式")
	private String locationContact;

	@ApiModelProperty("配送点联系地址")
	private String locationAddress;

	@ApiModelProperty("购买类型 购物车:cart 立即购买:normal")
	private String purchaseType;

	@ApiModelProperty("app_id")
	private String appId;

	@ApiModelProperty("union_id")
	private String unionId;

	@ApiModelProperty("所属分销商")
	private Long distributorId;

	@ApiModelProperty("购物车-订单项")
	private List<MallCartRequestQuery> listMallGoods;

	private String tenantId;

}
