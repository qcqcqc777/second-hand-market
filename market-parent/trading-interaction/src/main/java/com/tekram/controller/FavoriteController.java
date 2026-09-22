package com.tekram.controller;

import com.tekram.response.Result;
import com.tekram.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users/me/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PutMapping("/{itemId}")
    public Result favoriteItem(@RequestAttribute("currentUserId") Long userId,
                               @PathVariable Long itemId) {
        return Result.success(favoriteService.favoriteItem(userId, itemId));
    }

    @DeleteMapping("/{itemId}")
    public Result unfavoriteItem(@RequestAttribute("currentUserId") Long userId,
                                 @PathVariable Long itemId) {
        return Result.success(favoriteService.unfavoriteItem(userId, itemId));
    }

    @GetMapping("/{itemId}")
    public Result getFavoriteState(@RequestAttribute("currentUserId") Long userId,
                                   @PathVariable Long itemId) {
        return Result.success(favoriteService.getFavoriteState(userId, itemId));
    }

    @GetMapping
    public Result listFavorites(@RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestAttribute("currentUserId") Long userId) {
        return Result.success(favoriteService.listFavorites(page, pageSize, userId));
    }
}
