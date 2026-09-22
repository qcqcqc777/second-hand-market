package com.tekram.assembler;

import com.tekram.mapper.MessageMapper;
import com.tekram.pojo.Message;
import com.tekram.pojo.MessageVO;
import com.tekram.util.ImageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageVOAssembler {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ImageReference imageReference;

    /*
    配置author

     */
    public Map<String, MessageVO.AuthorVO> loadAuthorList(List<Message> messages) {
        // 只查询当前页涉及的作者，重复作者只查一次。
        //从留言列表中提取作者 ID，去掉 null 和重复值，得到一个作者 ID 列表
        List<Long> userIds = messages.stream()
                .map(Message::getUserId)    // :: 是方法引用,与message->message.getUserId()作用相同
                .filter(Objects::nonNull)   //过滤userId为null的值
                .distinct()                 //去掉重复userId
                .toList();                  //把收集的结果转换为列表
        /*以上代码相当于
        List<Long> userIds = new ArrayList<>();
        for (Message message : messages) {
            Long userId = message.getUserId();
            if (userId != null && !userIds.contains(userId)) {
                userIds.add(userId);
            }
        }
         */

        // 空列表不执行 SQL，避免出现 IN ()
        List<MessageVO.AuthorVO> authors = userIds.isEmpty()
                ? List.of()
                : messageMapper.selectAuthorsByIds(userIds);
        for (MessageVO.AuthorVO author : authors) {
            author.setAvatarUrl(imageReference.toUrl(author.getAvatarUrl()));
        }
        //把收集到的List转换为Map，
        Map<String, MessageVO.AuthorVO> authorMap = authors.stream()
                .collect(Collectors.toMap(  //collect() 表示把流中的元素收集成指定的结果,
                        // Collectors.toMap(...)就是告诉它：“按照指定规则，把作者收集成一个 Map。”
                        //toMap(如何获取键，如何获取值）
                        MessageVO.AuthorVO::getId,
                        Function.identity()     //author->author，输入什么就返回什么
                ));
    /*
    以上代码相当于
    Map<String, MessageVO.AuthorVO> authorMap = authors.stream()
    .collect(Collectors.toMap(
            author -> author.getId(), // 键：作者 ID
            author -> author          // 值：作者对象本身
    ));
    或者
    Map<String, MessageVO.AuthorVO> authorMap = new HashMap<>();

    for (MessageVO.AuthorVO author : authors) {
        authorMap.put(author.getId(), author);
    }
     */
        return authorMap;
    }

    /*
    组装MessageVOs
     */
    public List<MessageVO> toMessageVOs(List<Message> messages) {
        Map<String, MessageVO.AuthorVO> authorMap = loadAuthorList(messages);
        List<MessageVO> rows = new ArrayList<>();
        for (Message message : messages) {
            MessageVO.AuthorVO author = null;
            if (message.getUserId() != null) {  //有作者 ID 才去 Map 中找作者，没有就让 author 保持 null
                 author = authorMap.get(message.getUserId().toString());

            }
            rows.add(assemble(message, author));
        }
        return rows;
    }

    /*
    组装单个messagevo
     */
    public MessageVO toMessageVO(Message message) {
        // 复用批量查作者的方法，这里只传一个作者 ID
        MessageVO.AuthorVO author = messageMapper
                .selectAuthorsByIds(List.of(message.getUserId()))
                .stream()
                .findFirst()
                .orElse(null);

        if (author != null) {
            author.setAvatarUrl(imageReference.toUrl(author.getAvatarUrl()));
        }
        return assemble(message, author);
    }

    /*
     集中放置字段赋值逻辑，只做转换，不查询数据库
     */
    private MessageVO assemble(Message message, MessageVO.AuthorVO author) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId().toString());
        vo.setItemId(message.getItemId().toString());
        vo.setAuthor(author);
        vo.setParentId(message.getParentId() == null
                ? null : message.getParentId().toString());

        boolean deleted = Byte.valueOf((byte) 1).equals(message.getIsDeleted());
        vo.setDeleted(deleted);
        vo.setContent(deleted ? "该留言已删除" : message.getContent());

        if (message.getCreateTime() != null) {
            vo.setCreatedAt(
                    message.getCreateTime().atOffset(ZoneOffset.ofHours(8)));
        }
        return vo;

    }
}
