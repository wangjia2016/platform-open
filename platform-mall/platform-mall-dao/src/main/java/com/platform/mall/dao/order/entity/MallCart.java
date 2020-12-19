package com.platform.mall.dao.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_cart")
public class MallCart implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type=IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 用户编号
	 */
	private Long memberId;

	/**
	 * 商品编号
	 */
	private Long goodsId;

	/**
	 * 数量
	 */
	private Integer number;

	/**
	 * 
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
	 * 所属分销商
	 */
	private Long distributorId;

	/**
	 * 1 物流配送 2到店自提
	 */
	private String deliveryType;

	/**
	 * sku_id
	 */
	private Long skuId;

	/**
	 * sku_attr属性
	 */
	private String skuAttr;

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
