package me.wangao.community.controller;

import me.wangao.community.entity.Comment;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Event;
import me.wangao.community.event.EventProducer;
import me.wangao.community.service.CommentService;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.HostHolder;
import me.wangao.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Resource
    private CommentService commentService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private EventProducer eventProducer;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId())
                .setStatus(0)
                .setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        // 评论帖子，会修改回帖数量，触发 elasticsearch 修改帖子事件
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            Event updatePostEvent = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算帖子分数
            String scoreKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(scoreKey, discussPostId);
        }


        return "redirect:/discuss/detail/" + discussPostId;
    }
}
