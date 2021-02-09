package me.wangao.community.config;

import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow",
                        "/header/url"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/top", "/discuss/wonderful").hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete", "/data/**").hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll();

        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 认证异常处理（没有登录）
                    @Override
                    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException, ServletException {
                        String xRequestWith = req.getHeader("X-Requested-With");
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            // ajax请求
                            res.setContentType("application/plain; charset=utf-8");
                            res.getWriter().write(CommunityUtil.getJSONString(403, "您还没有登录"));
                        } else {
                            // 普通请求
                            res.sendRedirect(req.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 没有权限的异常处理
                    @Override
                    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestWith = req.getHeader("X-Requested-With");
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            // ajax请求
                            res.setContentType("application/plain; charset=utf-8");
                            res.getWriter().write(CommunityUtil.getJSONString(403, "您没有访问此功能的权限"));
                        } else {
                            // 普通请求
                            res.sendRedirect(req.getContextPath() + "/denied");
                        }
                    }
                });

        // security 默认会拦截logout进行退出的处理，需要覆盖其逻辑，执行自己的退出逻辑
        http.logout().logoutUrl("/security_logout");
    }
}
