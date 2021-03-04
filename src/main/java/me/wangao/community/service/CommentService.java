package me.wangao.community.service;

import me.wangao.community.dao.CommentMapper;
import me.wangao.community.entity.Comment;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.RedisKeyUtil;
import me.wangao.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private CounterService counterService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()))
                .setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        // 更新计数器
        counterService.incr(RedisKeyUtil.getCommentCounterKey());

        return rows;
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
