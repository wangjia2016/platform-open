package com.platform.mall.dao.goods.model.detail;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商品表DetailDto
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-13 15:20:48
 */
@ApiModel("商品表Detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallGoodsDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using  = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	@ApiModelProperty("")
	private Long id;
	/**
	 * 名称
	 */
	@ApiModelProperty("名称")
	private String name;
	/**
	 * 副标题
	 */
	@ApiModelProperty("副标题")
	private String title;
	/**
	 * 商品编码
	 */
	@ApiModelProperty("商品编码")
	private String code;

	/**
	 * 分类id
	 */
	@JsonSerialize(using  = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	@ApiModelProperty("分类id")
	private Long typeId;

	/**
	 * 父分类id
	 */
	@JsonSerialize(using  = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	@ApiModelProperty("父分类id")
	private Long parentTypeId;

	/**
	 * 品牌id
	 */
	@ApiModelProperty("品牌id")
	private Long brandId;
	/**
	 * （0-虚拟，1-实物）
	 */
	@ApiModelProperty("（0-虚拟，1-实物）")
	private Integer goodsType;
	/**
	 * 库存数量
	 */
	@ApiModelProperty("库存数量")
	private Integer stock;
	/**
	 * 库存预警值
	 */
	@ApiModelProperty("库存预警值")
	private Integer stockWarn;
	/**
	 * 状态 1待审核 2、审核通过 3审核失败
	 */
	@ApiModelProperty("状态 1待审核 2、审核通过 3审核失败")
	private Integer auditStatus;
	/**
	 * （0-上架，1-下架）
	 */
	@ApiModelProperty("（0-上架，1-下架）")
	private Integer onUnSale;
	/**
	 * 商品logo
	 */
	@ApiModelProperty("商品logo")
	private String logo;

	/**
	 * 商品多图地址
	 */
	@ApiModelProperty("商品多图地址")
	private String pictureUrl;

	/**
	 * 参考价
	 */
	@ApiModelProperty("参考价")
	private BigDecimal proposedPrice;
	/**
	 * 价格
	 */
	@ApiModelProperty("价格")
	private BigDecimal price;
	/**
	 * 分享描述
	 */
	@ApiModelProperty("分享描述")
	private String shareDesc;
	/**
	 * 是否推荐 0 推荐 1不推荐
	 */
	@ApiModelProperty("是否推荐 0 推荐 1不推荐")
	private Integer isRecommand;
	/**
	 * 是否支持退款 0支持 1不支持
	 */
	@ApiModelProperty("是否支持退款 0支持 1不支持")
	private Integer isRefund;
	/**
	 * 是否支持分账0支持 1不支持
	 */
	@ApiModelProperty("是否支持分账0支持 1不支持")
	private Integer isProfitsharing;
	/**
	 * 是否奖励积分
	 */
	@ApiModelProperty("是否奖励积分")
	private Integer isGetCredit;
	/**
	 * 奖励积分值
	 */
	@ApiModelProperty("奖励积分值")
	private Long creditValue;
	/**
	 * 是否开启虚拟销量
	 */
	@ApiModelProperty("是否开启虚拟销量")
	private Integer isSoldVirtual;
	/**
	 * 销量
	 */
	@ApiModelProperty("销量")
	private Long sold;
	/**
	 * 虚拟销量
	 */
	@ApiModelProperty("虚拟销量")
	private Long soldVirtual;
	/**
	 * 统一运费
	 */
	@ApiModelProperty("统一运费")
	private BigDecimal freight;
	/**
	 * 物流模板
	 */
	@ApiModelProperty("物流模板")
	private Long deliveryTemplateId;
	/**
	 * 限购
	 */
	@ApiModelProperty("限购")
	private Integer buyLimit;
	/**
	 * 最小购买限制
	 */
	@ApiModelProperty("最小购买限制")
	private Integer lowLimit;
	/**
	 * 虚拟物品类型:coupon-电子券，pack-大礼包
	 */
	@ApiModelProperty("虚拟物品类型:coupon-电子券，pack-大礼包")
	private String vrType;
	/**
	 * 虚拟物品代码
	 */
	@ApiModelProperty("虚拟物品代码")
	private String vrCode;
	/**
	 * 重量 默认为克
	 */
	@ApiModelProperty("重量 默认为克")
	private Integer weight;
	/**
	 * 单位
	 */
	@ApiModelProperty("单位")
	private String unit;

	/**
	 * 0 正常 1 删除
	 */
	@ApiModelProperty("0 正常 1 删除")
	private Integer isDelete;
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

	/**
	 * 商品介绍
	 */
	@ApiModelProperty("商品介绍 ")
	private String describeInfo;
}
