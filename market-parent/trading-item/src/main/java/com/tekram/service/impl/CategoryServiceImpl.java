package com.tekram.service.impl;

import com.tekram.mapper.CategoryMapper;
import com.tekram.pojo.Category;
import com.tekram.response.PageResult;
import com.tekram.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult listCategoris() {
        List<Category> list = categoryMapper.getCategory();
        return new PageResult((long) list.size(), list);

    }
}
