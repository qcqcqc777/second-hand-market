package com.tekram.controller;

import com.tekram.pojo.MessageDTO;
import com.tekram.response.Result;
import com.tekram.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/items/{itemId}/messages")
    public Result listMessages(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @PathVariable Long itemId) {
        return Result.success(messageService.listMessages(page, pageSize, itemId));

    }

    @PostMapping("/items/{itemId}/messages")
    public Result createMessage(@RequestBody MessageDTO  dto,
                                @PathVariable Long itemId,
                                @RequestAttribute("currentUserId") Long userId) {
        return Result.success(messageService.createMessage(dto, itemId, userId));
    }

    @DeleteMapping("/messages/{messageId}")
    public Result deleteMessage(@PathVariable Long messageId,
                                @RequestAttribute("currentUserId")  Long userId) {
        messageService.deleteMessage(messageId, userId);
        return Result.success();
    }

    @GetMapping("/users/me/messages")
    public Result listMyMessages(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestAttribute("currentUserId") Long userId) {
        return Result.success(messageService.listMyMessages(page, pageSize, userId));
    }

}
