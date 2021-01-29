package me.wangao.community.dao;

import me.wangao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") Integer userId);
}
