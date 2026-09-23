package com.tekram.support;

import com.tekram.mapper.OrderAddressMapper;
import com.tekram.pojo.OrderAddress;
import com.tekram.pojo.TradeOrder;
import com.tekram.pojo.TradeOrderVO;
import com.tekram.util.ImageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 批量加载订单地址快照，再统一转换单条或列表；不读取当前商品或用户地址簿。 */
@Component
public class TradeOrderVOAssembler {
    //定义北京时间的偏移量
    private static final ZoneOffset ORDER_OFFSET = ZoneOffset.ofHours(8);

    @Autowired
    private ImageReference imageReference;
    @Autowired
    private OrderAddressMapper orderAddressMapper;

    /** 只查询当前页涉及的订单地址，同一订单 ID 只查一次。 */
    public Map<Long, OrderAddress> loadAddressList(List<TradeOrder> orders) {
        List<Long> orderIds = orders.stream()
                .map(TradeOrder::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        // 空列表不执行 SQL，避免产生 IN ()。
        List<OrderAddress> addresses = orderIds.isEmpty()
                ? List.of()
                : orderAddressMapper.selectByOrderIds(orderIds);
        return addresses.stream().collect(Collectors.toMap(
                OrderAddress::getOrderId, Function.identity()));
    }

    /** 组装订单列表，保留原列表的顺序。 */
    public List<TradeOrderVO> toTradeOrderVOs(List<TradeOrder> orders) {
        Map<Long, OrderAddress> addressMap = loadAddressList(orders);
        List<TradeOrderVO> rows = new ArrayList<>();
        for (TradeOrder order : orders) {
            rows.add(assemble(order, requireAddress(addressMap, order.getId())));
        }
        return rows;
    }

    /** 组装单个订单，复用批量加载地址的方法。 */
    public TradeOrderVO toTradeOrderVO(TradeOrder order) {
        Objects.requireNonNull(order, "订单不能为空");
        Map<Long, OrderAddress> addressMap = loadAddressList(List.of(order));
        return assemble(order, requireAddress(addressMap, order.getId()));
    }

    /** 创建订单时已经持有地址快照，可直接转换，无须再次查询。 */
    public TradeOrderVO toTradeOrderVO(TradeOrder order, OrderAddress address) {
        return assemble(order, address);
    }

    private OrderAddress requireAddress(Map<Long, OrderAddress> addresses, Long orderId) {
        OrderAddress address = addresses.get(orderId);
        if (address == null) {
            throw new IllegalStateException("订单地址快照缺失，orderId=" + orderId);
        }
        return address;
    }

    /** 集中放置字段赋值，只做转换，不查询数据库。 */
    private TradeOrderVO assemble(TradeOrder order, OrderAddress address) {
        Objects.requireNonNull(order, "订单不能为空");
        Objects.requireNonNull(address, "订单地址快照不能为空");
        if (order.getId() == null || !order.getId().equals(address.getOrderId())) {
            throw new IllegalArgumentException("地址快照不属于当前订单");
        }

        TradeOrderVO vo = new TradeOrderVO();
        vo.setId(idToString(order.getId()));
        vo.setOrderNumber(order.getOrderNumber());
        vo.setBuyerId(idToString(order.getBuyerId()));
        vo.setSellerId(idToString(order.getSellerId()));
        vo.setOrderPrice(order.getOrderPrice());
        vo.setOrderStatus(statusName(order.getOrderStatus()));

        TradeOrderVO.ItemSnapshotVO itemVO = new TradeOrderVO.ItemSnapshotVO();
        itemVO.setItemId(idToString(order.getItemId()));
        itemVO.setItemNameSnapshot(order.getItemNameSnapshot());
        itemVO.setItemPriceSnapshot(order.getItemPriceSnapshot());
        itemVO.setItemCoverSnapshot(imageReference.toUrl(order.getItemCoverSnapshot()));
        vo.setItem(itemVO);

        TradeOrderVO.AddressSnapshotVO addressVO = new TradeOrderVO.AddressSnapshotVO();
        addressVO.setConsigneeName(address.getConsigneeName());
        addressVO.setConsigneePhone(address.getConsigneePhone());
        addressVO.setDetailAddress(address.getDetailAddress());
        vo.setAddress(addressVO);

        vo.setCreateTime(withOffset(order.getCreateTime()));
        vo.setExpireTime(withOffset(order.getExpireTime()));
        vo.setPaymentTime(withOffset(order.getPaymentTime()));
        vo.setDeliveryTime(withOffset(order.getDeliveryTime()));
        vo.setCompleteTime(withOffset(order.getCompleteTime()));
        vo.setCancelTime(withOffset(order.getCancelTime()));
        vo.setDeliveryNote(order.getDeliveryNote() == null ? "" : order.getDeliveryNote());
        vo.setCancelReason(order.getCancelReason() == null ? "" : order.getCancelReason());
        return vo;
    }

    private String idToString(Long id) {
        return id == null ? null : id.toString();
    }

    private OffsetDateTime withOffset(LocalDateTime time) {
        return time == null ? null : time.atOffset(ORDER_OFFSET);
    }

    private String statusName(Byte status) {
        if (status == null) {
            return null;
        }
        return switch (status.intValue()) {
            case 0 -> "WAIT_PAY";
            case 1 -> "WAIT_SHIP";
            case 2 -> "WAIT_RECEIVE";
            case 3 -> "COMPLETED";
            case 4 -> "CANCELLED";
            default -> throw new IllegalArgumentException("未知订单状态：" + status);
        };
    }
}
