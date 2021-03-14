package me.wangao.community.controller;

import me.wangao.community.entity.DiscussPost;
import me.wangao.community.entity.Node;
import me.wangao.community.entity.Page;
import me.wangao.community.entity.User;
import me.wangao.community.service.*;
import me.wangao.community.util.CommunityConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private NodeService nodeService;

    @Resource
    private CounterService counterService;

    @GetMapping({"/", "/index", "/index.html"})
    public String getIndexPage(Model model, Page page, @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        page.setRows(discussPostService.findDiscussPostRows(null));
        page.setPath("/index?orderMode=" + orderMode);

        model.addAttribute("orderMode", orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(null, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = getPostMapList(list);
        model.addAttribute("discussPosts", discussPosts);

        List<Node> nodes = nodeService.findAllNodes();
        model.addAttribute("nodes", nodes);

        return "index";
    }

    @GetMapping("/node/{id}")
    public String getNodePage(Model model, Page page, @PathVariable("id") int nodeId) {
        page.setRows(discussPostService.findDiscussPostRowsByNodeId(nodeId));
        page.setPath("/node/" + nodeId);
        model.addAttribute("nodeId", nodeId);
        //model.addAttribute("orderMode", -1);

        List<DiscussPost> list = discussPostService.findDiscussPostByNodeId(nodeId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = getPostMapList(list);

        model.addAttribute("discussPosts", discussPosts);

        List<Node> nodes = nodeService.findAllNodes();
        model.addAttribute("nodes", nodes);

        return "index";
    }

    private List<Map<String, Object>> getPostMapList(List<DiscussPost> list) {
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            list.forEach(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);

                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                Node node = nodeService.findNodeById(post.getNodeId());
//                do {
//                    node = nodeService.findNodeById(post.getNodeId());
//                } while (node == null);
                map.put("nodeName", node.getName());

                discussPosts.add(map);
            });
        }
        return discussPosts;
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
