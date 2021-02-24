package me.wangao.community.service;

import me.wangao.community.dao.NodeMapper;
import me.wangao.community.entity.Node;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NodeService {

    @Resource
    private NodeMapper nodeMapper;

    public Node findCommentById(int id) {
        return nodeMapper.selectById(id);
    }

    public List<Node> findAllNodes() {
        return nodeMapper.selectAllNodes();
    }
}
