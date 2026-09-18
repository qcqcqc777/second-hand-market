package com.tekram.controller;

import com.tekram.pojo.ItemDTO;
import com.tekram.pojo.ItemStatusUpdateDTO;
import com.tekram.pojo.ItemVO;
import com.tekram.response.PageResult;
import com.tekram.response.Result;
import com.tekram.service.ItemService;
import jakarta.validation.Valid;
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
                            String idleName, Long categoryId) {
        log.info("listItems:page={}, pageSize={},idleName={},categoryId={}", page, pageSize, idleName, categoryId);
        PageResult pageResult = itemService.listItems(page, pageSize, idleName, categoryId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result getItem(@PathVariable Long id) {
        log.info("getItem:{}", id);
        ItemVO item = itemService.getItemById(id);
        return Result.success(item);
    }

    @GetMapping("/me")
    public Result listMyItems(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize,
                              @RequestAttribute("currentUserId") Long userId) {
        log.info("listMyItems:{}", userId);
        PageResult pageResult = itemService.listMyItems(page, pageSize, userId);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result createItem(@RequestBody ItemDTO dto,
                             @RequestAttribute("currentUserId") Long userId) {
        log.info("createItem:{}", dto.toString());
        ItemVO itemVO = itemService.createItem(dto, userId);
        return Result.success(itemVO);
    }

    @PutMapping("/{itemId}")
    public Result updateItem(@PathVariable Long itemId,
                             @RequestAttribute("currentUserId") Long userId,
                             @RequestBody ItemDTO dto) {
        log.info("updateItem:{}", dto.toString());
        ItemVO itemVO = itemService.updateItem(dto, userId, itemId);
        return Result.success(itemVO);
    }

    @PutMapping("/{itemId}/status")
    public Result changeItemStatus(@Valid @RequestBody ItemStatusUpdateDTO dto,
                                   @PathVariable Long itemId,
                                   @RequestAttribute("currentUserId") Long userId) {
        log.info("changeItemStatus:{}", dto);
        ItemVO itemVO = itemService.changeItemStatus(dto,userId,itemId);
        return Result.success(itemVO);
    }

}
