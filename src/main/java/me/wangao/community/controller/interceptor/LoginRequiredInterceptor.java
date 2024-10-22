package me.wangao.community.controller.interceptor;

import me.wangao.community.annotation.LoginRequired;
import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Resource
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);

            if (loginRequired != null && hostHolder.getUser() == null) {
                String xReq = req.getHeader("x-requested-with");
                // 处理AJAX请求
                if (xReq.equals("XMLHttpRequest")) {
                    res.setContentType("application/plain; charset=utf-8");
                    res.getWriter().println(CommunityUtil.getJSONString(403, "请先登录"));
                } else {
                    res.sendRedirect(req.getContextPath() + "/login");
                }
                return false;
            }
        }
        return true;
    }
}
