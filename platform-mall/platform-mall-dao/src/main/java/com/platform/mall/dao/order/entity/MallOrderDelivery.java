package com.platform.mall.dao.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 订单物流信息
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_order_delivery")
public class MallOrderDelivery implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type=IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 订单编号
	 */
	private Long orderNo;

	/**
	 * 订单id
	 */
	private Long orderId;

	/**
	 * 会员编号
	 */
	private Long memberId;

	/**
	 * 用户openid
	 */
	private String openid;

	/**
	 * app_id
	 */
	private String appId;

	/**
	 * union_id
	 */
	private String unionId;

	/**
	 * 物流公司名称
	 */
	private String deliveryCompanyName;

	/**
	 * 物流编号
	 */
	private String deliveryNo;

	/**
	 * 发货时间
	 */
	private Date deliveryTime;

	/**
	 * 省
	 */
	private String province;

	/**
	 * 市
	 */
	private String city;

	/**
	 * 区
	 */
	private String area;

	/**
	 * 送货地址
	 */
	private String address;



	/**
	 * T true  F false
	 */
	@TableLogic
	private String isDelete;

	/**
	 * 创建人
	 */
	private String createUser;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改人
	 */
	private String updateUser;

	/**
	 * 修改时间
	 */
	private Date updateDatetime;

	/**
	 * 租户id
	 */
	private String tenantId;

}
