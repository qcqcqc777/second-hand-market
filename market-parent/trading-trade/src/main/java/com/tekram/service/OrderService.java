package com.tekram.service;

import com.tekram.pojo.OrderEnums;
import com.tekram.pojo.TradeOrderVO;
import com.tekram.response.PageResult;
import org.apache.ibatis.annotations.Param;

public interface OrderService {
    /**
     * 订单详情，仅买家或卖家可查看。
     */
    TradeOrderVO getOrder(Long orderId, Long userId);

    /*
    创建订单
     */
    TradeOrderVO createOrder(Long userId, Long itemId, Long addressId, String requestKey);

    /*
    查询订单列表
     */
    PageResult listOrders(Integer page, Integer pageSize, OrderEnums.Role role, OrderEnums.Status status, Long userId);

    /*
    买家取消订单
     */
    TradeOrderVO cancelOrder(Long orderId, String cancelReason, Long userId);

    /*
    卖家交付商品
     */
    TradeOrderVO shipOrder(Long orderId, String note, Long userId);

    /** 买家确认收货，已完成订单重复确认也返回成功。 */
    TradeOrderVO receiveOrder(Long orderId, Long userId);
}
