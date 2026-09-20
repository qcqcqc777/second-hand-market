package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.mapper.AddressMapper;
import com.tekram.mapper.ItemMapper;
import com.tekram.pojo.Item;
import com.tekram.pojo.ItemDTO;
import com.tekram.pojo.ItemStatusUpdateDTO;
import com.tekram.pojo.ItemVO;
import com.tekram.response.PageResult;
import com.tekram.service.ItemService;
import com.tekram.util.ImageReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ObjectMapper objectMapper;//负责转换数据格式
    @Autowired
    private ImageReference imageReference;


    @Override
    public PageResult listItems(Integer page, Integer pageSize, String idleName, Long categoryId) {
        //设置分页参数
        PageHelper.startPage(page, pageSize);
        //获取查询结果
        List<Item> items = itemMapper.listItems(idleName, categoryId);
        PageInfo<Item> pageInfo = new PageInfo<>(items);
        List<ItemVO> rows = toItemVOs(items);
        return new PageResult(pageInfo.getTotal(), rows);

    }

    @Override
    public ItemVO getItemById(Long id) {
        Item item = itemMapper.getItemById(id);
        if (item == null) {
            return null;
        }
        ItemVO.SellerVO seller = item.getUserId() == null ? null
                : itemMapper.listSellersByIds(List.of(item.getUserId())).stream()
                .findFirst().orElse(null);
        return toItemVO(item, seller);
    }


    @Override
    public PageResult listMyItems(Integer page, Integer pageSize, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        PageHelper.startPage(page, pageSize);
        List<Item> items = itemMapper.getItemsByUserId(userId);
        PageInfo<Item> pageInfo = new PageInfo<>(items);
        return new PageResult(pageInfo.getTotal(), toItemVOs(items));

    }

    @Override
    @Transactional
    public ItemVO createItem(ItemDTO dto, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }

        Item item = new Item();
        BeanUtils.copyProperties(dto, item);
        // DTO 使用列表，数据库保存 JSON 文本；BeanUtils 不会转换不同类型的属性。
        item.setPictureList(objectMapper.writeValueAsString(
                imageReference.toKeys(dto.getPictureList())));
        item.setUserId(userId);
        itemMapper.insertItem(item);
        ItemVO itemVO = getItemById(item.getId());
        return itemVO;

    }

    @Override
    @Transactional
    public ItemVO updateItem(ItemDTO dto, Long userId,Long itemId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        Item item = new Item();
        BeanUtils.copyProperties(dto, item);
        item.setUserId(userId);
        item.setId(itemId);
        item.setPictureList(objectMapper.writeValueAsString(
                imageReference.toKeys(dto.getPictureList())));
        itemMapper.updateItem(item);
        ItemVO itemVO = getItemById(itemId);
        return itemVO;
    }

    @Override
    public ItemVO changeItemStatus(ItemStatusUpdateDTO dto, Long userId, Long itemId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }

        int rows = itemMapper.updateItemStatus(dto,itemId,userId);
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "更新失败：商品不存在、不属于当前用户或版本已变化，请刷新后重试");
        }
        ItemVO itemVO = getItemById(itemId);
        return itemVO;
    }

    /** 批量补充卖家信息，三个查询接口使用相同的字段转换。 */
    private List<ItemVO> toItemVOs(List<Item> items) {
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
    private ItemVO toItemVO(Item item, ItemVO.SellerVO seller) {
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


    private List<String> parsePictureList(String pictureList) {
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
