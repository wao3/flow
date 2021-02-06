package me.wangao.community.controller;

import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.SearchPage;
import me.wangao.community.service.ElasticsearchService;
import me.wangao.community.service.LikeService;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        SearchPage<DiscussPost> result = elasticsearchService.searchDiscussPost(
                keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (result != null && !result.getList().isEmpty()) {
            result.getList().forEach(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            });
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword",keyword);

        page.setPath("/search?keyword=" + keyword);
        if (result == null) {
            page.setRows(0);
        } else {
            page.setRows((int) result.getTotal());
        }

        return "/site/search";
    }
}
