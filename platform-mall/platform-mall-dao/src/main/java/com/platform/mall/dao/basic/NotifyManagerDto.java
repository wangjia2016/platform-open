package com.platform.mall.dao.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author WangJia
 * @date 2020/11/13
 * 模板消息推送包装-推送给管理员
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyManagerDto {

    private String openid;

    private String url;

    private Long orderNo;

    private String price;

    private String remark;

    private String goodsName;

    private String address;

    private String appId;
}
