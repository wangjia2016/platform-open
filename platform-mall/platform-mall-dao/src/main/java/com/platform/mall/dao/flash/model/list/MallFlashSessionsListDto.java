package com.platform.mall.dao.flash.model.list;

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

@ApiModel("秒杀场次List")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsListDto implements Serializable {
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

    @ApiModelProperty("场次状态 0未开始 1进行中 2已结束")
    private Integer status;

    @ApiModelProperty("倒计时秒数(未开始=距开始,进行中=距结束,已结束=0)")
    private Long countdownSeconds;
}
