package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.User;
import cn.glh.alumni.service.LoginService;
import cn.glh.alumni.util.AlumniConstant;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Administrator
 * @Date: 2022/2/4 12:06
 * Description 登录、注册、登出
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private Producer kaptchaProducer;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private LoginService loginService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 进入注册界面
     * @return
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    /**
     * 进入登录界面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    /**
     * 进入重置密码界面
     * @return
     */
    @GetMapping("/resetPwd")
    public String getResetPwdPage() {
        return "reset-pwd";
    }

    /**
     * 进入首页
     * @return
     */
    @GetMapping("/index")
    public String getIndexPage() {
        return "index";
    }

    /**
     * 注册用户
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = loginService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功, 我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", map.get("msg"));
            model.addAttribute("target", "/user/register");
        }
        return "operate-result";
    }


    /**
     * 激活用户
     * @param model
     * @param code 激活码
     * @return
     * http://localhost:8080/alumni/user/activation/用户id/激活码
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = loginService.activation(userId, code);
        if (result == AlumniConstant.ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功, 您的账号已经可以正常使用!");
            model.addAttribute("target", "/user/login");
        }
        else if (result == AlumniConstant.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效的操作, 您的账号已被激活过!");
            model.addAttribute("target", "/user/index");
        }
        else {
            model.addAttribute("msg", "激活失败, 您提供的激活码不正确!");
            model.addAttribute("target", "/user/index");
        }
        return "operate-result";
    }

    /**
     * 进入登录页面, 生成验证码, 并存入 Redis
     * @param response
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response) {
        // 生成随机字符
        String text = kaptchaProducer.createText();
        System.out.println("验证码：" + text);
        // 生成图片
        BufferedImage image = kaptchaProducer.createImage(text);

        // 验证码的归属者
        String kaptchaOwner = AlumniUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入 redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败", e.getMessage());
        }
    }

    /**
     * 验证用户输入的图片验证码是否和redis中存入的是否相等
     *
     * @param kaptchaOwner 从 cookie 中取出的 kaptchaOwner
     * @param checkCode 用户输入的图片验证码
     * @return 失败则返回原因, 验证成功返回 "",
     */
    private String checkKaptchaCode(String kaptchaOwner, String checkCode) {
        if (StringUtils.isBlank(checkCode)) {
            return "未发现输入的图片验证码";
        }
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        String kaptchaValue = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(kaptchaValue)) {
            return "图片验证码过期";
        } else if (!kaptchaValue.equalsIgnoreCase(checkCode)) {
            return "图片验证码错误";
        }
        return "";
    }

    /**
     * 用户登录
     * @param userName 用户名
     * @param pwd 密码
     * @param code 验证码
     * @param model
     * @param kaptchaOwner 从 cookie 中取出的 kaptchaOwner
     * @param response
     * @return
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("userName") String userName, @RequestParam("pwd") String pwd,
                        @RequestParam("code") String code,
                        Model model,
                        HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        //验证码不正确、退回到登录页面
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("msg", "验证码错误");
            model.addAttribute("target", "/user/login");
            return "operate-result";
        }

        // 验证用户名和密码
        Map<String, Object> map = loginService.login(userName, pwd);
        if (map.containsKey("ticket")) {
            // 账号和密码均正确，则服务端会生成 ticket，浏览器通过 cookie 存储 ticket
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            // cookie 有效范围
            cookie.setPath(contextPath);
            // cookie 有效时间 12小时
            cookie.setMaxAge(AlumniConstant.DEFAULT_EXPIRED_SECONDS);
            response.addCookie(cookie);
            return "redirect:/user/index";
        }
        //登录失败
        else {
            model.addAttribute("msg", map.get("msg"));
            model.addAttribute("target", "/user/login");
            return "operate-result";
        }

    }

    /**
     * 用户登出
     * @param ticket 设置凭证状态为无效
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        loginService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "/index";
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPwd")
    public String resetPwd(@RequestParam("userName") String userName,
                                        @RequestParam("pwd") String pwd,
                                        @RequestParam("emailVerifyCode") String emailVerifyCode,
                                        @RequestParam("code") String kaptcha,
                                        Model model,
                                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        Map<String, Object> map = new HashMap<>(4);
        // 检查图片验证码
        String kaptchaCheckRst = checkKaptchaCode(kaptchaOwner, kaptcha);
        if (StringUtils.isNotBlank(kaptchaCheckRst)) {
            model.addAttribute("msg",kaptchaCheckRst);
            model.addAttribute("target", "/user/resetPwd");
            return "operate-result";
        }
        // 检查邮件验证码
        String emailVerifyCodeCheckRst = checkRedisResetPwdEmailCode(userName, emailVerifyCode);
        if (StringUtils.isNotBlank(emailVerifyCodeCheckRst)) {
            model.addAttribute("msg",emailVerifyCodeCheckRst);
            model.addAttribute("target", "/user/resetPwd");
            return "operate-result";
        }
        // 执行重置密码操作
        Map<String, Object> stringObjectMap = loginService.doResetPwd(userName, pwd);
        String userNameMsg = (String) stringObjectMap.get("errMsg");
        if (StringUtils.isBlank(userNameMsg)) {
            model.addAttribute("msg",userNameMsg);
            model.addAttribute("target", "/user/login");
        }
        return "operate-result";
    }

    /**
     * 发送邮件验证码(用于重置密码)
     *
     * @param kaptchaOwner 从 cookie 中取出的 kaptchaOwner
     * @param kaptcha 用户输入的图片验证码
     * @param userName 用户输入的需要找回的账号
     */
    @PostMapping("/sendEmailCodeForResetPwd")
    @ResponseBody
    public Map<String, Object> sendEmailCodeForResetPwd(Model model, @CookieValue("kaptchaOwner") String kaptchaOwner,
                                                        @RequestParam("kaptcha") String kaptcha,
                                                        @RequestParam("userName") String userName) {
        Map<String, Object> map = new HashMap<>(3);
        // 检查图片验证码
        String kaptchaCheckRst = checkKaptchaCode(kaptchaOwner, kaptcha);
        if (StringUtils.isNotBlank(kaptchaCheckRst)) {
            map.put("status", "1");
            map.put("errMsg", kaptchaCheckRst);
        }
        Map<String, Object> stringObjectMap = loginService.doSendEmailCode4ResetPwd(userName);
        String usernameMsg = (String) stringObjectMap.get("errMsg");
        if (StringUtils.isBlank(usernameMsg)) {
            map.put("status", "0");
            map.put("msg", "已经往您的邮箱发送了一封验证码邮件, 请查收!");
        }
        return map;
    }

    /**
     * 检查 邮件 验证码
     *
     * @param userName 用户名
     * @param checkCode 用户输入的图片验证码
     * @return 验证成功 返回"", 失败则返回原因
     */
    private String checkRedisResetPwdEmailCode(String userName, String checkCode) {
        if (StringUtils.isBlank(checkCode)) {
            return "未发现输入的邮件验证码";
        }
        final String redisKey = "EmailCode4ResetPwd:" + userName;
        String emailVerifyCodeInRedis = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(emailVerifyCodeInRedis)) {
            return "邮件验证码已过期";
        } else if (!emailVerifyCodeInRedis.equalsIgnoreCase(checkCode)) {
            return "邮件验证码错误";
        }
        return "";
    }


}
