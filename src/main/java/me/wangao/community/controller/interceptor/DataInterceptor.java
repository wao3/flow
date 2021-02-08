package me.wangao.community.controller.interceptor;

import me.wangao.community.entity.User;
import me.wangao.community.service.DataService;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Resource
    private DataService dataService;

    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // 统计UV
        String ip = req.getRemoteHost();
        dataService.recordUV(ip);
        // 统计DAU
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
