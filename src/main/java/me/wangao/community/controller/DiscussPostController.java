package me.wangao.community.controller;

import me.wangao.community.entity.Comment;
import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.User;
import me.wangao.community.service.CommentService;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.LikeService;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String discussPost(String title, String content) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录");
        }

        DiscussPost post = new DiscussPost()
                .setUserId(user.getId())
                .setTitle(title)
                .setContent(content);

        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @GetMapping("/detail/{id}")
    public String getDiscussPost(@PathVariable int id, Model model, Page page) {
        // 帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPost);

        // 作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("author", user);

        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);

        // 点赞状态
        if (user == null) {
            model.addAttribute("LikeStatus", 0);
        } else {
            int LikeStatus = likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST, id);
            model.addAttribute("LikeStatus", LikeStatus);
        }


        //  评论分页信息
        page.setLimit(5)
                .setPath("/discuss/detail/" + id)
                .setRows(discussPost.getCommentCount());

        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            commentList.forEach(comment -> {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment", comment);
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                // 点赞数量
                long commentLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", commentLikeCount);

                // 点赞状态
                int likeStatus = 0;
                if (hostHolder.getUser() != null) {
                    likeStatus = likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                }
                commentVO.put("likeStatus", likeStatus);

                // 查评论的二级评论（回复）
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();

                if(replyList != null) {
                    replyList.forEach(reply -> {
                        Map<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply", reply);
                        replyVO.put("user", userService.findUserById(reply.getUserId()));

                        // 回复的点赞数量
                        replyVO.put("likeCount",
                                likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));

                        // 回复的点赞状态
                        if (hostHolder.getUser() != null) {
                            replyVO.put("likeStatus",
                                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId()));
                        } else {
                            replyVO.put("likeStatus", 0);
                        }


                        // 回复的目标
                        if (reply.getTargetId() != null && reply.getTargetId() != 0) {
                            User target = userService.findUserById(reply.getTargetId());
                            replyVO.put("target", target);
                        } else {
                            replyVO.put("target", null);
                        }
                        replyVOList.add(replyVO);
                    });
                }

                commentVO.put("replies", replyVOList);

                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);

                commentVOList.add(commentVO);
            });
        }

        model.addAttribute("comments", commentVOList);

        return "/site/discuss-detail";
    }
}
