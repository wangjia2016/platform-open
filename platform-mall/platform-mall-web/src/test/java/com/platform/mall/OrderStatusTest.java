package com.platform.mall;

/**
 * @Description 订单状态流转测试
 * @ClassName OrderStatusTest
 * @Author wangjia
 * @date 2022.02.26 21:39
 */

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes= OrderStatusTest.class)
//@ComponentScan({"com.platform.mall","com.platform.common.*"})
public class OrderStatusTest {

//    {
//        System.setProperty("nacos.logging.default.config.enabled", "false");
//    }
//
//    private Logger logger = LoggerFactory.getLogger(OrderStatusTest.class);
//
//
//    @Autowired
//    private IStateHandler stateHandler;
//
//    @Test
//    public void test(){
//
//
//        final Result result = stateHandler.toReceive(6827125726869852160L, OrderStatusEnum.TO_PAY);
//
//        logger.info(result.toString());
//
//    }

    //@Test
    public static void main(String args[]){
        SingleEnum instance = SingleEnum.INSTANCE;
        instance.test();
    }
}
