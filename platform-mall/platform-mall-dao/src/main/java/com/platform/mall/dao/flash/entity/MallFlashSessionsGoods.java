package com.platform.mall.dao.flash.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀场次关联商品（同一商品可挂多个场次）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_flash_sessions_goods")
public class MallFlashSessionsGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long sessionsId;

    private Long goodsId;

    private BigDecimal flashPrice;

    private Integer flashStock;

    private Integer listOrder;

    @TableLogic
    private Integer isDelete;

    private String createUser;

    private Date createTime;

    private String updateUser;

    private Date updateDatetime;

    private String tenantId;
}
