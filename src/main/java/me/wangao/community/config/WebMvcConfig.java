package me.wangao.community.config;

import me.wangao.community.controller.interceptor.LoginRequiredInterceptor;
import me.wangao.community.controller.interceptor.LoginTicketInterceptor;
import me.wangao.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;

    @Resource
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Resource
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");
    }
}
