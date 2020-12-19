package com.platform.mall.dao.goods.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 商品表
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:02:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_goods")
public class MallGoods implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type=IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 名称
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String name;

	/**
	 * 副标题
	 */
	private String title;

	/**
	 * 商品编码
	 */
	private String code;

	/**
	 * 分类id
	 */
	private Long typeId;

	/**
	 * 父分类id
	 */
	private Long parentTypeId;

	/**
	 * 品牌id
	 */
	private Long brandId;

	/**
	 * 库存数量
	 */
	private Integer stock;

	/**
	 * 库存预警值
	 */
	private Integer stockWarn;

	/**
	 * 状态 1待审核 2、审核通过 3审核失败
	 */
	private Integer auditStatus;

	/**
	 * （0-上架，1-下架）
	 */
	private Integer onUnSale;

	/**
	 * 商品logo
	 */
	private String logo;

	/**
	 * 参考价
	 */
	private BigDecimal proposedPrice;

	/**
	 * 价格
	 */
	private BigDecimal price;

	/**
	 * 是否支持退款 0支持 1不支持
	 */
	private Integer isRefund;

	/**
	 * 重量 默认为克
	 */
	private Integer weight;

	/**
	 * 单位
	 */
	private String unit;


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
