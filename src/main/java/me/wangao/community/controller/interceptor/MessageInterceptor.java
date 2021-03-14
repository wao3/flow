package me.wangao.community.controller.interceptor;

import me.wangao.community.entity.User;
import me.wangao.community.service.*;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor, CommunityConstant {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private MessageService messageService;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @Resource
    private CounterService counterService;

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int userId = user.getId();
            // 未读消息
            int letterUnreadCount = messageService.findLetterUnreadCount(userId, null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);

            // 点赞数量
            int likeCount = likeService.findUserLikeCount(userId);
            modelAndView.addObject("myLikeCount", likeCount);

            // 关注数量
            long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
            modelAndView.addObject("myFolloweeCount", followeeCount);

            // 粉丝数量
            long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
            modelAndView.addObject("myFollowerCount", followerCount);
        }

        if (modelAndView != null) {
            // 侧边计数器
            int userCount = counterService.getUserCount();
            int commentCount = counterService.getCommentCount();
            int postCount = counterService.getPostCount();
            modelAndView.addObject("userCount", userCount);
            modelAndView.addObject("commentCount", commentCount);
            modelAndView.addObject("postCount", postCount);
        }

    }
}
