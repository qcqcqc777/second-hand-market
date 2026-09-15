package com.tekram.service;

import com.tekram.pojo.Item;
import com.tekram.response.PageResult;

import java.util.List;

public interface ItemService {
    /*
    分页查询商品信息
     */
    PageResult listItems(Integer page, Integer pageSize, String idleName, Integer categoryId);

    /*
    根据id查询商品-商品详情页
     */
    Item getItemById(Integer id);
}
