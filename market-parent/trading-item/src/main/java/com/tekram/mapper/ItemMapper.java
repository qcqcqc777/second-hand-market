package com.tekram.mapper;

import com.tekram.pojo.Item;
import com.tekram.pojo.ItemStatusUpdateDTO;
import com.tekram.pojo.ItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    /*
    分页查询-公开商品页
     */
    List<Item> listItems(@Param("idleName") String idleName,
                         @Param("categoryId") Long categoryId);

    /*
    根据id查询商品-商品详情页
     */
    Item getItemById(@Param("id") Long id);
    List<ItemVO.SellerVO> listSellersByIds(@Param("userIds") List<Long> userIds);
    /*
    根据用户id查询商品-我的发布列表
     */
    List<Item> getItemsByUserId(@Param("userId") Long userIds);
    /*
    新增商品
     */
    int insertItem(Item item);
    /*
    修改商品
     */
    int updateItem(Item item);
    /*
    修改商品状态
     */
    int updateItemStatus(@Param("dto") ItemStatusUpdateDTO dto, @Param("itemId") Long itemId, @Param("userId") Long userId);

}
