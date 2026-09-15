package com.tekram.controller;

import com.tekram.pojo.Category;
import com.tekram.response.PageResult;
import com.tekram.response.Result;
import com.tekram.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result listCategories() {
        PageResult pageResult = categoryService.listCategoris();
        return Result.success(pageResult);
    }
}
