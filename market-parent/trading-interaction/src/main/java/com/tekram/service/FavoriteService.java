package com.tekram.service;

import com.tekram.pojo.FavoriteVO;
import com.tekram.response.PageResult;

public interface FavoriteService {
    /*
    收藏商品
     */
    FavoriteVO favoriteItem(Long userId, Long itemId);
    /*
    取消收藏
     */
    FavoriteVO unfavoriteItem(Long userId, Long itemId);
    /*
    检查收藏
     */
    FavoriteVO getFavoriteState(Long userId, Long itemId);
    /*
    收藏列表
     */
    PageResult listFavorites(Integer page, Integer pageSize,Long userId);
}
