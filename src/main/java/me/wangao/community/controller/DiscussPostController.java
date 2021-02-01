package me.wangao.community.controller;

import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.User;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

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
    public String getDiscussPost(@PathVariable int id, Model model) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPost);

        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("author", user);

        return "/site/discuss-detail";
    }
}
