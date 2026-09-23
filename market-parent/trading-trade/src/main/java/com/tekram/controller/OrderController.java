package com.tekram.controller;

import com.tekram.pojo.OrderEnums;
import com.tekram.pojo.TradeOrderVO;
import com.tekram.pojo.TraderOrderDTO;
import com.tekram.response.Result;
import com.tekram.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public Result getOrder(@PathVariable Long orderId,
                           @RequestAttribute("currentUserId") Long userId) {
        return Result.success(orderService.getOrder(orderId, userId));
    }

    @PostMapping
    public Result createOrder(@RequestAttribute("currentUserId") Long userId,
                              @RequestHeader("Idempotency-Key") String requestKey,
                              @RequestBody TraderOrderDTO dto) {
        return Result.success(orderService.createOrder(userId, dto.getItemId(), dto.getAddressId(), requestKey));
    }

    @GetMapping
    public Result listOrders(@RequestAttribute("currentUserId") Long userId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "BUYER") OrderEnums.Role role,
                             @RequestParam(required = false) OrderEnums.Status status) {
        return Result.success(orderService.listOrders(page, pageSize, role, status, userId));
    }

    @PostMapping("/{orderId}/cancel")
    public Result cancelOrder(@PathVariable Long orderId,
                              @RequestBody Map<String, String> body,
                              @RequestAttribute("currentUserId") Long userId) {

        String reason = body.get("reason");

        // 调用取消订单业务
        return Result.success(orderService.cancelOrder(orderId, reason, userId));
    }

    @PostMapping("/{orderId}/receipt")
    public Result receiveOrder(@PathVariable Long orderId,
                               @RequestAttribute("currentUserId") Long userId) {
        return Result.success(orderService.receiveOrder(orderId, userId));
    }

    @PostMapping("/{orderId}/shipment")
    public Result shipOrder(@PathVariable Long orderId,
                            @RequestBody Map<String, String> body,
                            @RequestAttribute("currentUserId") Long userId) {
        String note = body.get("handoverNote");
        return Result.success(orderService.shipOrder(orderId, note, userId));
    }
}
