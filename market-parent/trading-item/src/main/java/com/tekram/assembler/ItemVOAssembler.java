package com.tekram.assembler;

import com.tekram.mapper.ItemMapper;
import com.tekram.pojo.Item;
import com.tekram.pojo.ItemVO;
import com.tekram.util.ImageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ItemVOAssembler {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageReference imageReference;

    // 把下面提到的三个方法搬到这里
    /** 批量补充卖家信息，三个查询接口使用相同的字段转换。 */
    public List<ItemVO> toItemVOs(List<Item> items) {
        // 一次查出当前页所有卖家，再逐个转换商品。
        List<Long> userIds = items.stream().map(Item::getUserId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, ItemVO.SellerVO> sellers = userIds.isEmpty() ? Map.of()
                : itemMapper.listSellersByIds(userIds).stream()
                  .collect(Collectors.toMap(ItemVO.SellerVO::getId, Function.identity()));
        List<ItemVO> rows = items.stream()
                .map(item -> toItemVO(item, item.getUserId() == null
                        ? null : sellers.get(item.getUserId())))
                .toList();
        return rows;
    }

    /** 只负责单个商品的字段转换，不查询数据库。 */
    public ItemVO toItemVO(Item item, ItemVO.SellerVO seller) {
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setIdleName(item.getIdleName());
        vo.setIdleDetails(item.getIdleDetails());
        vo.setIdlePrice(item.getIdlePrice());
        vo.setCategoryId(item.getCategoryId());
        vo.setIdlePlace(item.getIdlePlace());
        vo.setPictureList(parsePictureList(item.getPictureList()));
        if (seller != null) {
            seller.setAvatarUrl(imageReference.toUrl(seller.getAvatarUrl()));
        }
        vo.setSeller(seller);
        vo.setItemStatus(item.getItemStatus());
        vo.setModerationStatus(item.getModerationStatus());
        vo.setVersion(item.getVersion());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }


    public List<String> parsePictureList(String pictureList) {
        if (pictureList == null || pictureList.isBlank()) {
            return List.of();
        }
        JsonNode images = objectMapper.readTree(pictureList);
        if (!images.isArray()) {
            throw new IllegalArgumentException("pictureList 必须是图片路径 JSON 数组");
        }
        List<String> paths = new ArrayList<>();
        for (JsonNode image : images) {
            if (!image.isString()) {
                throw new IllegalArgumentException("pictureList 中的图片路径必须是字符串");
            }
            paths.add(imageReference.toUrl(image.asString()));
        }
        return paths;
    }
}