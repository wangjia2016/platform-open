package com.platform.mall.dao.goods.model.query;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商品表Query
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-13 15:20:48
 */
@ApiModel("商品表Query")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallGoodsQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
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
	@ApiModelProperty("分类id")
	private Long typeId;

	/**
	 * 父分类id
	 */
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
	 * 是否推荐 0 推荐 1不推荐
	 */
	@ApiModelProperty("是否推荐 0 推荐 1不推荐")
	private Integer isRecommand;
	/**
	 * 是否支持退款 0支持 1不支持
	 */
	@ApiModelProperty("是否支持退款 0支持 1不支持")
	private Integer isRefund;

	@ApiModelProperty("库存")
	private Integer stock;

	/**
	 * 参考价
	 */
	@ApiModelProperty("参考价")
	private BigDecimal proposedPrice;

	@ApiModelProperty("价格")
	private BigDecimal price;

	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 租户id
	 */
	@ApiModelProperty("租户id")
	private String tenantId;
}
