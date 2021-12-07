package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : Real
 * @date : 2021/12/5 17:39
 * @description :
 */
@Mapper
public interface MessageMapper {

    /**
     * 分页查询会话列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询用户会话数量
     *
     * @param userId 用户 ID
     * @return 会话数量
     */
    int selectConversationsCount(int userId);


    /**
     * 查询一个会话中所有的私信列表
     *
     * @param conversationId 会话 ID
     * @param offset         起始
     * @param limit          分页数量
     * @return 私信集合列表
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询一个会话中消息的数量
     *
     * @param conversationId 会话 ID
     * @return 数量
     */
    int selectedLetterCount(String conversationId);

    /**
     * 查询未读消息的数量，包含一个会话列表中的未读数量，该用户对应的所有私信的未读数量
     * 在编写 Sql 的时候需要动态传递 conversationId ，决定使用哪一种
     *
     * @param userId         用户 ID
     * @param conversationId 会话 ID
     * @return 私信列表中未读消息的数量
     */
    int selectLetterUnreadCount(int userId, String conversationId);

}
