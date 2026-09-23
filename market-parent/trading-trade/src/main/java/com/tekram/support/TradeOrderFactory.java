package com.tekram.support;

import com.tekram.pojo.Item;
import com.tekram.pojo.TradeOrder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/** 组装新增订单的入库参数，不负责查询、业务校验或保存。 */
@Component
public class TradeOrderFactory {

    private final ObjectMapper objectMapper;

    public TradeOrderFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** 创建订单前即可计算摘要，供旧订单的幂等检查使用。 */
    public String buildRequestHash(Long buyerId, Long itemId, Long addressId) {
        String content = "buyerId=" + buyerId + "&itemId=" + itemId + "&addressId=" + addressId;
        return sha256(content);
    }

    /**
     * 使用已校验的商品生成订单，自动提取商品首图，无图时封面为 null。
     * requestKey 和 requestHash 由调用方传入，便于在创建前检查重复请求。
     */
    public TradeOrder create(
            Long buyerId,
            Item item,
            String requestKey,
            String requestHash) {

        TradeOrder order = new TradeOrder();

        order.setOrderNumber(
                "M" + UUID.randomUUID().toString().replace("-", "")
        );
        order.setBuyerId(buyerId);
        order.setSellerId(item.getUserId());
        order.setItemId(item.getId());

        order.setItemNameSnapshot(item.getIdleName());
        order.setItemCoverSnapshot(firstPicture(item.getPictureList()));
        order.setItemPriceSnapshot(item.getIdlePrice());
        order.setOrderPrice(item.getIdlePrice());

        order.setRequestKey(requestKey);
        order.setRequestHash(requestHash);

        // 状态、版本和时间由 insertOrder SQL 设置；id 回填，exclusiveItemId 由数据库生成。
        return order;
    }

    /** pictureList 保存的是 JSON 数组；无图时封面为 null。 */
    private String firstPicture(String pictureList) {
        if (pictureList == null || pictureList.isBlank()) {
            return null;
        }
        JsonNode pictures = objectMapper.readTree(pictureList);
        if (!pictures.isArray()) {
            throw new IllegalArgumentException("pictureList 必须是图片路径 JSON 数组");
        }
        if (pictures.isEmpty()) {
            return null;
        }
        JsonNode cover = pictures.get(0);
        if (!cover.isString() || cover.asString().isBlank()) {
            throw new IllegalArgumentException("商品封面必须是非空图片路径");
        }
        return cover.asString();
    }

    /** 将请求内容转换为 64 位十六进制摘要；相同内容生成相同摘要，不是加密。 */
    private String sha256(String content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", e);
        }
    }
}
