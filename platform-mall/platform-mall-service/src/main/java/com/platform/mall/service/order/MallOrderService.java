package com.platform.mall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.platform.common.result.Result;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.model.list.MallOrderListDto;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MallOrder业务处理
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
public interface MallOrderService extends IService<MallOrder> {

    /**
     * @Description 根据MallOrderDto查询一条MallOrder记录
     * @Param MallOrderDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:06:55
     **/
    MallOrderDetailDto getOne(MallOrderQuery mallOrderQuery);

    /**
     * @Description 根据MallOrderDto查询一条MallOrder记录
     * @Param MallOrderDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:06:55
     **/
    MallOrderDetailDto orderDetail(Long id);

    /**
     * @Description 普通-提交订单
     * @Param mallOrderRequestQuery
     * @return
     * @Author wangjia
     * @Date 2020-10-6 17:06:55
     **/
    Boolean createOrder(MallOrderRequestQuery mallOrderRequestQuery);

    /**
     * @Description 预扣库存
     * @Param goodsId
     * @Param num
     * @return Boolean
     * @Author wangjia
     * @Date 2020-10-6 17:06:55
     **/
    Boolean preReduceStock(MallOrderRequestQuery mallOrderRequestQuery);

    /**
     * @Description 库存校驗
     * @Param goodsId
     * @Param num
     * @return Boolean
     * @Author wangjia
     * @Date 2020-11-7 17:06:55
     **/
    Boolean checkStock(MallOrderRequestQuery mallOrderRequestQuery);

    /**
     * @Description 取消订单
     * @Param Long id
     * @return Boolean
     * @Author wangjia
     * @Date 2020-11-8 11:30:55
     **/
    Result cancelOrder(Long id, String reason);

    /**
     * 异步发送微信模板消息处理
     * */
    void asyncMsgProcess(MallOrder mallOrder);


    Boolean preCreateOrder(MallOrderRequestQuery mallOrderRequest);
}

