package com.tekram.controller;

import com.tekram.pojo.Item;
import com.tekram.response.PageResult;
import com.tekram.response.Result;
import com.tekram.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @GetMapping
    public Result listItems(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "20") Integer pageSize,
                            String idleName, Integer categoryId) {
        log.info("listItems:page={}, pageSize={},idleName={},categoryId={}", page, pageSize, idleName, categoryId);
        PageResult pageResult=itemService.listItems(page, pageSize, idleName, categoryId);
        return Result.success(pageResult);
    }
    @GetMapping("/{id}")
    public Result getItem(@PathVariable Integer id) {
        Item item = itemService.getItemById(id);
        return Result.success(item);
    }

}
