package com.tekram.mapper;

import com.tekram.pojo.Favorite;
import com.tekram.pojo.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    /*
    增加收藏
     */
    int insertFavorite(@Param("userId") Long userId, @Param("itemId") Long itemId);
    /*
    删除收藏
     */
    int deleteFavorite(@Param("userId") Long userId, @Param("itemId") Long itemId);
    /*
    查询收藏
     */
    Favorite selectFavorite(@Param("userId") Long userId, @Param("itemId") Long itemId);
    /*
    查询收藏列表
     */
    List<Item> selectListFavorites(@Param("userId") Long userId);
}
