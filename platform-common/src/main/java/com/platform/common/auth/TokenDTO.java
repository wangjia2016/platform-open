package com.platform.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description token返回DTO
 * @Author wangjia
 * @ClassName TokenDTO
 * @Date 2020.03.07 12:01
 **/
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TokenDTO {
    /**
     * token
     */
    private String token;
    /**
     * 过期时间
     */
    private Long expirationTime;
}
