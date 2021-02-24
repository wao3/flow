package me.wangao.community.dao;

import me.wangao.community.entity.Node;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NodeMapper {

    @Select("select id, `name`, `desc` from node where id = #{id}")
    Node selectById(int id);

    @Select("select id, `name`, `desc` from node")
    List<Node> selectAllNodes();

    @Insert("insert into node(`name`, `desc`) values(#{name}, #{desc})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertNode(Node node);
}
