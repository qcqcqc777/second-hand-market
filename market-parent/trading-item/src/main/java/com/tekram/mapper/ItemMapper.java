package com.tekram.mapper;

import com.tekram.pojo.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    /*
    分页查询-公开商品页
     */
    public List<Item> listItems(@Param("idleName") String idleName,
                                @Param("categoryId") Integer categoryId);

    /*
    根据id查询商品-商品详情页
     */
    public Item getItemById(@Param("id") Integer id);

}
