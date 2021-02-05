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

    // 新增消息
    int insertMessage(Message message);

    // 修改消息状态
    int updateStatus(List<Integer> ids, int status);

    // 查询某个主题下最新的通知
    @Select({
            "select id, from_id, to_id, conversation_id, content, status, create_time ",
            "from message ",
            "where from_id=1 and to_id=#{userId} and conversation_id=#{topic} and status != 2",
            "order by create_time desc ",
            "limit 1"
    })
    Message selectLatestNotice(int userId, String topic);

    // 查询某主题下的通知的数量
    @Select({
            "select count(id) from message ",
            "where from_id=1 and to_id=#{userId} and conversation_id=#{topic} and status != 2"
    })
    int selectNoticeCount(int userId, String topic);

    // 查询未读通知的数量
    int selectNoticeUnreadCount(int userId, @Param("topic") String topic);

    // 查询某主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
