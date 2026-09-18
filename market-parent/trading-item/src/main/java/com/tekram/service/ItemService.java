package com.tekram.service;

import com.tekram.pojo.Item;
import com.tekram.pojo.ItemDTO;
import com.tekram.pojo.ItemStatusUpdateDTO;
import com.tekram.pojo.ItemVO;
import com.tekram.response.PageResult;

import java.util.List;

public interface ItemService {
    /*
    分页查询商品信息
     */
    PageResult listItems(Integer page, Integer pageSize, String idleName, Long categoryId);

    /*
    根据id查询商品-商品详情页
     */
    ItemVO getItemById(Long id);

    /*
    用户发布商品列表
     */
    PageResult listMyItems(Integer page, Integer pageSize, Long userId);
    /*
    发布商品
     */
    ItemVO createItem(ItemDTO itemDTO,Long userId);
    /*
    编辑商品
     */
    ItemVO updateItem(ItemDTO itemDTO,Long userId,Long itemId);
    /*
    更新商品状态
     */
    ItemVO changeItemStatus(ItemStatusUpdateDTO dto, Long userId, Long itemId);
}
