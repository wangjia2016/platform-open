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
import java.util.Date;

/**
 * 秒杀场次
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mall_flash_sessions")
public class MallFlashSessions implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private Date startTime;

    private Date endTime;

    private Long flashPromotionId;

    private Integer buyLimit;

    /** 0 不启用 1启用 */
    private Integer isEnable;

    private String remark;

    private Integer listOrder;

    @TableLogic
    private Integer isDelete;

    private String createUser;

    private Date createTime;

    private String updateUser;

    private Date updateDatetime;

    private String tenantId;
}
