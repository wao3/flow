package me.wangao.community.dao;

import me.wangao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") Integer userId);

    @Insert({
            "insert into discuss_post(user_id, title, content, type, status, create_time, comment_count, score) ",
            "values(#{userId}, #{title}, #{content}, #{type}, #{status}, #{createTime}, #{commentCount}, #{score})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDiscussPost(DiscussPost discussPost);

    @Select({
            "select id, user_id, title, content, type, status, create_time, comment_count, score ",
            "from discuss_post ",
            "where id = #{id} "
    })
    DiscussPost selectDiscussPostById(int id);

    @Update("update discuss_post set comment_count = #{commentCount} where id = #{id}")
    int updateCommentCount(int id, int commentCount);

    @Update("update discuss_post set type = #{type} where id = #{id}")
    int updateType(int id, int type);

    @Update("update discuss_post set status = #{status} where id = #{id}")
    int updateStatus(int id, int status);
}
