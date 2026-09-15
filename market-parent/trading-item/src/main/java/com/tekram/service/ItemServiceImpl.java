package com.tekram.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.mapper.ItemMapper;
import com.tekram.pojo.Item;
import com.tekram.response.PageResult;
import com.tekram.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult listItems(Integer page, Integer pageSize, String idleName, Integer categoryId) {
        //设置分页参数
        PageHelper.startPage(page, pageSize);
        //获取查询结果
        List<Item> items = itemMapper.listItems(idleName, categoryId);
        PageInfo<Item> pageInfo = new PageInfo<>(items);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());

    }

    @Override
    public Item getItemById(Integer id) {
        Item item = itemMapper.getItemById(id);
        return item;
    }
}
