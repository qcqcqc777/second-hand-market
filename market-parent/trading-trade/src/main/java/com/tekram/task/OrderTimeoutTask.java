package com.tekram.task;

import com.tekram.mapper.OrderMapper;
import com.tekram.pojo.TradeOrder;
import com.tekram.service.OrderTimeoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderTimeoutTask {
    private static final int BATCH_SIZE = 100;
    private static final int MAX_BATCHES = 10;
    // 达到单轮上限时下轮接着扫描，避免前面的异常订单长期挡住后面的订单。
    private long scanAfterId;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderTimeoutService orderTimeoutService;

    // 启动后等待一分钟；每轮结束后再等待一分钟，可通过配置调整。
    @Scheduled(fixedDelayString = "${order.timeout.scan-delay-ms:60000}",
            initialDelayString = "${order.timeout.initial-delay-ms:60000}")
    public void cancelExpiredOrders() {
        long afterId = scanAfterId;
        int cancelled = 0;
        for (int batch = 0; batch < MAX_BATCHES; batch++) {
            List<TradeOrder> orders = orderMapper.selectExpiredOrders(afterId, BATCH_SIZE);
            if (orders.isEmpty()) {
                scanAfterId = 0L;
                break;
            }
            for (TradeOrder order : orders) {
                afterId = order.getId();
                try {
                    // 调用另一个 Spring Bean，确保每笔订单的事务生效。
                    if (orderTimeoutService.cancelExpiredOrder(order.getId(), order.getItemId())) {
                        cancelled++;
                    }
                } catch (RuntimeException e) {
                    log.error("超时取消订单失败，下轮重试，orderId={}", order.getId(), e);
                }
            }
            if (orders.size() < BATCH_SIZE) {
                scanAfterId = 0L;
                break;
            }
            scanAfterId = afterId;
        }
        if (cancelled > 0) {
            log.info("本轮超时取消订单 {} 笔", cancelled);
        }
    }
}
