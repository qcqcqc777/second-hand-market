package com.tekram.mapper;

import com.tekram.pojo.Message;

import com.tekram.pojo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    /*查询当前商品的留言列表*/
    List<Message> selectListMessages(@Param("itemId") Long itemId);

    /*批量查询用户信息*/
    List<MessageVO.AuthorVO> selectAuthorsByIds(
            @Param("userIds") List<Long> userIds);

    /*新增留言*/
    int insertMessage(Message message);

    /*根据id查询留言*/
    Message selectMessageById(@Param("id") Long id);

    /*删除留言*/
    int deleteMessage(@Param("id") Long id, @Param("userId") Long userId);

    /*查询收到的留言列表*/
    List<Message> selectMyMessages(@Param("userId") Long userId);
}
