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
 * 
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-23 20:55:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_order")
public class MallOrder implements Serializable {
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
	 * 会员编号
	 */
	private Long memberId;

	/**
	 * 商品总价
	 */
	private BigDecimal goodsPrice;

	/**
	 * 物流运费
	 */
	private BigDecimal freight;

	/**
	 * 积分抵扣金额
	 */
	private BigDecimal deductionAmount;

	/**
	 * 优惠券抵扣金额
	 */
	private BigDecimal couponAmount;

	/**
	 * 实付金额
	 */
	private BigDecimal actualAmount;

	/**
	 * 后台手工优惠金额
	 */
	private BigDecimal concessionaryAmount;

	/**
	 * 总计金额
	 */
	private BigDecimal totalAmount;

	/**
	 * 使用积分
	 */
	private Long useCredit;

	/**
	 * 1 物流配送 2到店自提
	 */
	private Integer deliveryType;

	/**
	 * 储值支付金额
	 */
	private BigDecimal rechargeBalance;

	/**
	 * 付款方式:1、在线付款 2、货到付款
	 */
	private Integer payType;

	/**
	 * 付款时间
	 */
	private Date payTime;

	/**
	 * 支付状态 0 已支付 1 未支付 
	 */
	private Integer payStatus;

	/**
	 * 1待付款   2 待发货 3 待收货 4待评价 5申请退款 6 退款成功 7交易成功 8交易关闭
	 */
	private Integer orderStatus;

	/**
	 * 订单关闭原因
	 */
	private String closeReason;

	/**
	 * 联系人
	 */
	private String receiver;

	/**
	 * 联系方式
	 */
	private String contact;

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
	 * 微信支付订单号
	 */
	private String transactionId;

	/**
	 * 所属分销商
	 */
	private Long distributorId;

	/**
	 * 订单备注
	 */
	private String remark;

	/**
	 * 申请退款时间
	 */
	private Date applyRefundTime;

	/**
	 * 退款单号
	 */
	private String refundId;

	/**
	 * 退款原因
	 */
	private String refundDesc;

	/**
	 * 退款时间
	 */
	private Date refundTime;

	/**
	 * 使用优惠券信息
	 */
	private String couponAttr;

	/**
	 * 预订时间
	 */
	private Date reserveTime;

	/**
	 * 业务类型 1 普通订单 2 秒杀订单 3拼团订单
	 */
	private Integer businessType;

	/**
	 * 1微信公众号 2微信小程序 3PC 4android 5 ios
	 */
	private Integer clientType;

	/**
	 * 商品类型  1 实物 、2 虚拟
	 */
	private Integer goodsType;

	/**
	 * 订单来源
	 */
	private String original;

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
