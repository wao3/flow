package me.wangao.community.dao;

import me.wangao.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每个会话只返回最新一条私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话包含的对话数量
    int selectLetterCount(String conversationId);

    // 查询未读私信数量
    int selectLetterUnreadCount(int userId, @Param("conversationId") String conversationId);

}
