package me.wangao.community.controller;

import com.google.code.kaptcha.Producer;
import me.wangao.community.entity.User;
import me.wangao.community.service.UserService;
import me.wangao.community.util.CommunityConstant;
import me.wangao.community.util.CommunityUtil;
import me.wangao.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    UserService userService;

    @Resource
    Producer kaptchaProducer;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse res, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        // session.setAttribute("captcha", text);

        // 将验证码存入redis
        String captchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("captchaOwner", captchaOwner);
        cookie.setPath(contextPath);
        res.addCookie(cookie);
        String captchaKey = RedisKeyUtil.getCaptchaKey(captchaOwner);
        redisTemplate.opsForValue().set(captchaKey, text, 60, TimeUnit.SECONDS);


        // 将图片输出给浏览器
        res.setContentType("image/png");
        try (ServletOutputStream os = res.getOutputStream()) {
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("生成验证码失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "您已经激活过了，请勿重复激活");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }

        return "/site/operate-result";
    }

    @PostMapping("/login")
    public String login(String username, String password, String captcha, boolean rememberMe,
                        Model model, HttpServletResponse res, @CookieValue String captchaOwner) {
        // 检查验证码
        // String realCaptcha = (String) session.getAttribute("captcha");
        String realCaptcha = null;

        if (StringUtils.isNotBlank(captchaOwner)) {
            String captchaKey = RedisKeyUtil.getCaptchaKey(captchaOwner);
            realCaptcha = (String)redisTemplate.opsForValue().get(captchaKey);
        }

        if (StringUtils.isBlank(realCaptcha) || StringUtils.isBlank(captcha) || !realCaptcha.equalsIgnoreCase(captcha)) {
            model.addAttribute("captchaMsg", "验证码不正确");
            return "/site/login";
        }

        // 检查账号
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECOND : DEFAULT_EXPIRED_SECOND;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            res.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            return "/site/login";
        }
    }

    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }
}
