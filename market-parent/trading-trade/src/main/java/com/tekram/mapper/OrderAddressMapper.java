package com.tekram.mapper;

import com.tekram.pojo.OrderAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderAddressMapper {
    /*
    新增订单地址
     */
    int insertOrderAddress(@Param("orderAddress") OrderAddress orderAddress);

    /** 读取订单创建时保存的地址快照。 */
    OrderAddress selectByOrderId(@Param("orderId") Long orderId);

    /** 批量读取当前页订单的地址快照。 */
    List<OrderAddress> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}
