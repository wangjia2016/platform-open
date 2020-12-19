package com.platform.mall.dao.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 订单项，订单包含的商品、订单详情
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_order_item")
public class MallOrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type=IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 订单id
	 */
	private Long orderId;

	/**
	 * 订单编号
	 */
	private Long orderNo;

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
	 * 商品编号
	 */
	private Long goodsId;

	/**
	 * 数量
	 */
	private Integer number;

	/**
	 * 商品成本价
	 */
	private BigDecimal costPrice;

	/**
	 * 价格
	 */
	private BigDecimal price;

	/**
	 * 储值支付金额
	 */
	private BigDecimal rechargeBalance;

	/**
	 * 优惠价格
	 */
	private BigDecimal concessionaryPrice;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品logo
	 */
	private String goodsLogo;

	/**
	 * sku_id
	 */
	private Long skuId;

	/**
	 * sku_attr属性
	 */
	private String skuAttr;

	/**
	 * 所属分销商
	 */
	private Long distributorId;

	/**
	 * 0 正常 1 删除
	 */
	@TableLogic
	private Integer isDelete;

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
