package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.assembler.MessageVOAssembler;
import com.tekram.mapper.MessageMapper;
import com.tekram.pojo.Message;
import com.tekram.pojo.MessageDTO;
import com.tekram.pojo.MessageVO;
import com.tekram.response.PageResult;
import com.tekram.service.ItemService;
import com.tekram.service.MessageService;
import com.tekram.util.ImageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ImageReference imageReference;

    @Autowired
    private ItemService itemService;

    @Autowired
    private MessageVOAssembler messageVOAssembler;

    @Override
    public PageResult listMessages(Integer page, Integer pageSize, Long itemId) {
        // 先分页查询留言，并保留原始分页结果中的总数。
        PageHelper.startPage(page, pageSize);
        List<Message> messages = messageMapper.selectListMessages(itemId);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);
        //组装messagevo
        List<MessageVO> rows = messageVOAssembler.toMessageVOs(messages);
        return new PageResult(pageInfo.getTotal(), rows);
    }

    @Override
    @Transactional
    public MessageVO createMessage(MessageDTO dto, Long itemId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        Long toUserId = itemService.getItemById(itemId).getSeller().getId();
        Message message = new Message();
        message.setUserId(userId);
        message.setItemId(itemId);
        message.setContent(dto.getContent());
        message.setParentId(dto.getParentId() == null ? null : dto.getParentId());
        message.setToUserId(toUserId);
        messageMapper.insertMessage(message);

        //获取新创建的message
        Message saved = messageMapper.selectMessageById(message.getId());

        return messageVOAssembler.toMessageVO(saved);


    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        messageMapper.deleteMessage(messageId, userId);

    }

    @Override
    @Transactional
    public PageResult listMyMessages(Integer page, Integer pageSize, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        PageHelper.startPage(page, pageSize);
        List<Message> messages = messageMapper.selectMyMessages(userId);
        PageInfo<Message> pageInfo = new PageInfo<>(messages);
        //组装messagevo
        List<MessageVO> rows = messageVOAssembler.toMessageVOs(messages);
        return new PageResult(pageInfo.getTotal(), rows);

    }
}
