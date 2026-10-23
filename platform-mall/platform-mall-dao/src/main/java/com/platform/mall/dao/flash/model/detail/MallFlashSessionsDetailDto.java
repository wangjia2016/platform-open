package com.platform.mall.dao.flash.model.detail;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@ApiModel("秒杀场次Detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("场次id")
    private Long id;

    @ApiModelProperty("场次名称")
    private String name;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("关联活动")
    private Long flashPromotionId;

    @ApiModelProperty("限购")
    private Integer buyLimit;

    @ApiModelProperty("0 不启用 1启用")
    private Integer isEnable;

    @ApiModelProperty("描述")
    private String remark;

    @ApiModelProperty("排序")
    private Integer listOrder;

    @ApiModelProperty("租户id")
    private String tenantId;
}
