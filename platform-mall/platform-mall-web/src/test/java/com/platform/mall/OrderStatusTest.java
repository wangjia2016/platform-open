package com.platform.mall;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.IStateHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;


/**
 * @Description 订单状态流转测试
 * @ClassName OrderStatusTest
 * @Author wangjia
 * @date 2022.02.26 21:39
 */

//@RunWith(SpringRunner.class)
@SpringBootTest(classes= OrderStatusTest.class)
@ComponentScan({"com.platform.mall","com.platform.common.*"})
public class OrderStatusTest {

    {
        System.setProperty("nacos.logging.default.config.enabled", "false");
    }

    private Logger logger = LoggerFactory.getLogger(OrderStatusTest.class);


    @Autowired
    private IStateHandler stateHandler;

    @Test
    public void test(){


        final Result result = stateHandler.toReceive(6827125726869852160L, OrderStatusEnum.TO_PAY);

        logger.info(result.toString());

    }
}
