package com.platform.mall.dao.order.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Query
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
@ApiModel("用户订单列表查询参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderPortalQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员编号
	 */
	@ApiModelProperty("会员编号")
	private Long memberId;

	/**
	 * 支付状态 0 已支付 1 未支付 
	 */
	@ApiModelProperty("支付状态 0 已支付 1 未支付 ")
	private Integer payStatus;

	/**
	 * 1待付款   2 待发货 3 待收货 4待评价 5申请退款 6 退款成功 7交易成功 8交易关闭
	 */
	@ApiModelProperty("1待付款   2 待发货 3 待收货 4待评价 5申请退款 6 退款成功 7交易成功 8交易关闭")
	private Integer orderStatus;

	/**
	 * 1 普通订单 2 秒杀订单 3拼团订单
	 */
	@ApiModelProperty("1 普通订单 2 秒杀订单 3拼团订单")
	private Integer orderType;

	/**
	 * 用户openid
	 */
	@ApiModelProperty("用户openid")
	private String openid;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("开始时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 修改时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("结束时间")
	private Date endTime;

	/**
	 * 所属分销商
	 */
	@ApiModelProperty("所属分销商")
	private Long distributorId;
}
