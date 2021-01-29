package me.wangao.community.controller;

import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.User;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(null));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(null, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            list.forEach(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);

                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            });
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }
}
