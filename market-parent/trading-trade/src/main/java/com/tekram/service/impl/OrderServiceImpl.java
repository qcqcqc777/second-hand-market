package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.mapper.AddressMapper;
import com.tekram.mapper.ItemMapper;
import com.tekram.mapper.OrderAddressMapper;
import com.tekram.mapper.OrderMapper;
import com.tekram.pojo.*;
import com.tekram.response.PageResult;
import com.tekram.service.OrderService;
import com.tekram.support.TradeOrderFactory;
import com.tekram.support.TradeOrderVOAssembler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private TradeOrderFactory tradeOrderFactory;
    @Autowired
    private TradeOrderVOAssembler assemblerVO;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderAddressMapper orderAddressMapper;

    @Override
    public TradeOrderVO getOrder(Long orderId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (orderId == null || orderId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        }
        TradeOrder order = orderMapper.selectByOrderId(orderId);
        // 不存在和无权访问统一返回 404，确认权限后才加载地址快照。
        if (order == null || (!Objects.equals(order.getBuyerId(), userId)
                && !Objects.equals(order.getSellerId(), userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        }
        return assemblerVO.toTradeOrderVO(order);
    }

    @Override
    @Transactional
    public TradeOrderVO createOrder(Long userId, Long itemId, Long addressId, String requestKey) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (itemId == null || itemId <= 0 || addressId == null || addressId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品和地址 ID 必须为正数");
        }
        if (requestKey == null || requestKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "下单请求键不能为空");
        }

        // 摘要可用于后续查询旧订单并比较请求参数，无须先创建新订单。
        String requestHash = tradeOrderFactory.buildRequestHash(userId, itemId, addressId);
        //查询数据库中是否有该用户下requestKey相同的旧订单
        TradeOrder oldOrder = orderMapper.selectByRequestKey(userId, requestKey);
        //判断旧订单是否和当前新订单是相同的
        if (oldOrder != null) {
            if (!Objects.equals(oldOrder.getRequestHash(), requestHash)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "同一个请求键不能用于不同的下单参数");
            }
            //旧订单与新订单requestHash相同
            return assemblerVO.toTradeOrderVO(oldOrder);
        }
        Item item = itemMapper.getItemById(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "商品不存在");
        }
        if (Objects.equals(item.getUserId(), userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不允许购买自己的商品");
        }
        if (!Byte.valueOf((byte) 1).equals(item.getItemStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "商品状态不允许购买");
        }
        //设置订单地址orderAddress
        Address address = addressMapper.selectOwnedAddress(addressId, userId);
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "收货地址不存在或不属于当前用户");
        }
        // 条件更新占用商品，与订单、地址快照写入同一事务；失败时一起回滚。
        if (itemMapper.occupyItem(itemId, item.getVersion()) != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "商品状态或信息已变化，请刷新后重试");
        }
        TradeOrder tradeOrder = tradeOrderFactory.create(userId, item, requestKey, requestHash);
        orderMapper.insertOrder(tradeOrder);
        OrderAddress orderAddress = new OrderAddress();
        BeanUtils.copyProperties(address, orderAddress, "id", "createTime");//复制相同的字段值，排除id和createTime
        orderAddress.setOrderId(tradeOrder.getId());
        // 快照保存完整地址，之后修改地址簿不会影响历史订单。
        orderAddress.setDetailAddress(Stream.of(address.getProvinceName(), address.getCityName(),
                        address.getRegionName(), address.getDetailAddress())
                .filter(Objects::nonNull)
                .collect(Collectors.joining()));
        orderAddressMapper.insertOrderAddress(orderAddress);
        //返回新订单
        return assemblerVO.toTradeOrderVO(orderMapper.selectByOrderId(tradeOrder.getId()), orderAddress);
    }

    @Override
    public PageResult listOrders(Integer page, Integer pageSize, OrderEnums.Role role, OrderEnums.Status status, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }

        PageHelper.startPage(page, pageSize);
        List<TradeOrder> orders;
        Integer statusCode = status == null ? null : status.getCode();
        orders = role == OrderEnums.Role.SELLER
                ? orderMapper.selectSellerOrders(userId, statusCode)
                : orderMapper.selectBuyerOrders(userId, statusCode);
        PageInfo<TradeOrder> pageInfo = new PageInfo<>(orders);
        List<TradeOrderVO> rows = assemblerVO.toTradeOrderVOs(orders);
        return new PageResult(pageInfo.getTotal(), rows);
    }

    @Override
    public TradeOrderVO cancelOrder(Long orderId, String cancelReason, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        int rows = orderMapper.updateOrderStatus(orderId, cancelReason, userId);

        if (rows != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "订单不存在、不属于当前买家或已不处于待付款状态"
            );
        }
        return assemblerVO.toTradeOrderVO(orderMapper.selectByOrderId(orderId));
    }

    @Override
    @Transactional
    public TradeOrderVO receiveOrder(Long orderId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (orderId == null || orderId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        }
        // SQL 同时检查买家身份和待收货状态，并记录服务端完成时间。
        int rows = orderMapper.updateOrderCompleted(orderId, userId);
        TradeOrder order = orderMapper.selectByOrderId(orderId);
        if (order == null || !Objects.equals(order.getBuyerId(), userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在");
        }
        // 已完成订单重复确认直接返回原数据，不刷新完成时间。
        if (rows != 1 && !Byte.valueOf((byte) 3).equals(order.getOrderStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "订单不处于待收货状态");
        }
        return assemblerVO.toTradeOrderVO(order);
    }

    @Override
    public TradeOrderVO shipOrder(Long orderId, String note, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        int rows = orderMapper.updateOrderRecieve(orderId, note, userId);

        if (rows != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "订单不存在、不属于当前买家或已不处于待交付状态"
            );
        }
        return assemblerVO.toTradeOrderVO(orderMapper.selectByOrderId(orderId));
    }

}
