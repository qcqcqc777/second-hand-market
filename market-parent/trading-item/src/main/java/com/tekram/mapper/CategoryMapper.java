package com.tekram.mapper;

import com.tekram.pojo.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> getCategory();
}
