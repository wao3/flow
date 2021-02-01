package me.wangao.community.service;

import me.wangao.community.dao.DiscussPostMapper;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DiscussPostService {

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(Integer userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(Integer userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()))
                .setContent(HtmlUtils.htmlEscape(post.getContent()))
                // 过滤敏感词
                .setTitle(sensitiveFilter.filter(post.getTitle()))
                .setContent(sensitiveFilter.filter(post.getContent()))
                // 设置其他字段
                .setCreateTime(new Date())
                .setType(0)
                .setStatus(0)
                .setCommentCount(0)
                .setScore(0.);

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }
}
