package com.platform.mall.dao.flash.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("秒杀场次查询参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallFlashSessionsQuery {

    @ApiModelProperty("场次id")
    private Long id;

    @ApiModelProperty("场次名称")
    private String name;

    @ApiModelProperty("关联活动")
    private Long flashPromotionId;

    @ApiModelProperty("0 不启用 1启用；门户列表默认只查启用")
    private Integer isEnable;

    @ApiModelProperty("租户id")
    private String tenantId;
}
