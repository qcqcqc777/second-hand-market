package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.assembler.ItemVOAssembler;
import com.tekram.mapper.FavoriteMapper;
import com.tekram.pojo.Favorite;
import com.tekram.pojo.FavoriteVO;
import com.tekram.pojo.Item;
import com.tekram.pojo.ItemVO;
import com.tekram.response.PageResult;
import com.tekram.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ItemVOAssembler itemVOAssembler;

    @Override
    public FavoriteVO favoriteItem(Long userId, Long itemId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        favoriteMapper.insertFavorite(userId, itemId);
        return new FavoriteVO(true);


    }

    @Override
    public FavoriteVO unfavoriteItem(Long userId, Long itemId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        favoriteMapper.deleteFavorite(userId, itemId);
        return new FavoriteVO(false);
    }

    @Override
    public FavoriteVO getFavoriteState(Long userId, Long itemId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        if (favoriteMapper.selectFavorite(userId, itemId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NOT_FOUND");
        }
        return new FavoriteVO(true);
    }

    @Override
    public PageResult listFavorites(Integer page, Integer pageSize, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        PageHelper.startPage(page, pageSize);
        List<Item> items = favoriteMapper.selectListFavorites(userId);
        PageInfo<Item> pageInfo = new PageInfo<>(items);
        // 复用现有商品列表的转换逻辑
        List<ItemVO> rows = itemVOAssembler.toItemVOs(items);
        return new PageResult(pageInfo.getTotal(), rows);
    }
}
