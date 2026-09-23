package com.tekram.mapper;

import com.tekram.pojo.TradeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    /*新增订单*/
    int insertOrder(TradeOrder tradeOrder);

    /*根据requestKey和buyerId查询订单*/
    TradeOrder selectByRequestKey(@Param("buyerId") Long buyerId, @Param("requestKey") String requestKey);

    /*根据订单id查询订单*/
    TradeOrder selectByOrderId(@Param("orderId") Long orderId);

    List<TradeOrder> selectExpiredOrders(@Param("afterId") Long afterId, @Param("limit") int limit);

    int cancelExpiredOrder(@Param("orderId") Long orderId, @Param("itemId") Long itemId);

    int insertTimeoutStatusLog(@Param("orderId") Long orderId);

    /*查询买家订单列表*/
    List<TradeOrder> selectBuyerOrders(@Param("buyerId") Long buyerId, @Param("status") Integer orderStatus);

    /*查询卖家订单列表*/
    List<TradeOrder> selectSellerOrders(@Param("sellerId") Long sellerId, @Param("status") Integer orderStatus);

    /*修改订单状态为取消*/
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("cancelReason") String cancelReason, @Param("userId") Long userId);

    /*修改订单状态为待收货*/
    int updateOrderRecieve(@Param("orderId") Long orderId,@Param("note") String deliveryNote, @Param("userId") Long userId);

    /** 仅买家可将待收货订单更新为已完成。 */
    int updateOrderCompleted(@Param("orderId") Long orderId, @Param("userId") Long userId);

}
