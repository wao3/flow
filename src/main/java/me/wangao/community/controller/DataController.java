package me.wangao.community.controller;

import me.wangao.community.service.DataService;
import me.wangao.community.service.DiscussPostService;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DataController {

    @Resource
    private DataService dataService;

    @Resource
    private UserService userService;

    @Resource
    private DiscussPostService discussPostService;

    @RequestMapping("/data")
    public String getDataPage(Model model) {
        int todayRegisterCount = userService.findTodayRegisterCount();
        int todayPostCount = discussPostService.findTodayPostCount();
        model.addAttribute("todayRegisterCount",todayRegisterCount);
        model.addAttribute("todayPostCount",todayPostCount);


        long todayUV = dataService.getTodayUV();
        long todayDAU = dataService.getTodayDAU();
        model.addAttribute("todayUV", todayUV);
        model.addAttribute("todayDAU", todayDAU);

        return "/site/admin/data";
    }

    // 统计网站uv
    @PostMapping("/data/uv")
    @ResponseBody
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long count = dataService.calculateUV(start, end);
        List<Map<String, Object>> list = dataService.listUV(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("list", list);

        return CommunityUtil.getJSONString(0, "获取成功", result);
    }

    // 统计网站dau
    @PostMapping("/data/dau")
    @ResponseBody
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long count = dataService.calculateDAU(start, end);
        List<Map<String, Object>> list = dataService.listDAU(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("list", list);

        return CommunityUtil.getJSONString(0, "获取成功", result);
    }
}
