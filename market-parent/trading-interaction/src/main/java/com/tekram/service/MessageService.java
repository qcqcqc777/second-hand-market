package com.tekram.service;

import com.tekram.pojo.MessageDTO;
import com.tekram.pojo.MessageVO;
import com.tekram.response.PageResult;

public interface MessageService {
    /*
    商品留言列表
     */
    PageResult listMessages(Integer page,Integer pageSize,Long itemId);
    /*
    发布留言
     */
    MessageVO createMessage(MessageDTO dto,Long itemId,Long userId);
    /*
    删除自己的留言
     */
    void deleteMessage(Long messageId,Long userId);
    /*收到的留言*/
    PageResult listMyMessages(Integer page,Integer pageSize,Long userId);
}
