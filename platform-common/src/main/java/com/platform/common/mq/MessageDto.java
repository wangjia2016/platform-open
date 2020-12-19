package com.platform.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description mq 消息封装
 * @ClassName MessageDto
 * @Author wangjia
 * @date 2020.09.05 11:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto<T> {

    /**
     * 消息id
     * */
    private String msgId;

    /**
     * 事务id
     * */
    private String transactionId;

    /**
     * 消息体
     * */
    private T payLoad;
}
