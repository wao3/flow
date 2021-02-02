package me.wangao.community.controller.advice;

import me.wangao.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(annotations = {Controller.class, RestController.class})
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({ Exception.class })
    public void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) throws IOException {
        logger.error("服务器异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xReq = req.getHeader("x-requested-with");
        // 处理AJAX请求
        if (xReq.equals("XMLHttpRequest")) {
            res.setContentType("application/plain; charset=utf-8");
            res.getWriter().println(CommunityUtil.getJSONString(500, "服务器异常!"));
        } else {
            res.sendRedirect(req.getContextPath() + "/error");
        }
    }
}
