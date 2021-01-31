package me.wangao.community.dao;

import me.wangao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") Integer userId);

    @Insert({
            "insert into discuss_post(user_id, title, content, type, status, create_time, comment_count, score) ",
            "values(#{userId}, #{title}, #{content}, #{type}, #{status}, #{createTime}, #{commentCount}, #{score})"
    })
    int insertDiscussPost(DiscussPost discussPost);
}
