package com.tekram.service.impl;

import com.tekram.mapper.ItemMapper;
import com.tekram.mapper.OrderMapper;
import com.tekram.pojo.Item;
import com.tekram.service.OrderTimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderTimeoutServiceImpl implements OrderTimeoutService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ItemMapper itemMapper;

    /** 独立事务，取消、释放商品、状态日志必须一起成功。 */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean cancelExpiredOrder(Long orderId, Long itemId) {
        // 与创建订单统一锁顺序：先商品，后订单。
        Item item = itemMapper.getItemByIdForUpdate(itemId);
        if (item == null) {
            throw new IllegalStateException("订单关联商品不存在：" + itemId);
        }
        if (orderMapper.cancelExpiredOrder(orderId, itemId) == 0) {
            return false;
        }

        if (Byte.valueOf((byte) 3).equals(item.getItemStatus())) {
            if (itemMapper.releaseOrderItem(itemId) != 1) {
                throw new IllegalStateException("释放订单占用商品失败：" + itemId);
            }
        } else if (!Byte.valueOf((byte) 1).equals(item.getItemStatus())) {
            // 兼容旧代码创建的、未把商品设为占用的订单；其他状态不擅自覆盖。
            throw new IllegalStateException("订单关联商品状态异常：" + itemId);
        }
        if (orderMapper.insertTimeoutStatusLog(orderId) != 1) {
            throw new IllegalStateException("记录订单超时取消日志失败：" + orderId);
        }
        return true;
    }
}
