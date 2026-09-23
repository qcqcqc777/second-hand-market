package com.tekram.service;

public interface OrderTimeoutService {
    /** 返回 true 表示本次取消成功；false 表示订单已不符合超时取消条件。 */
    boolean cancelExpiredOrder(Long orderId, Long itemId);
}
