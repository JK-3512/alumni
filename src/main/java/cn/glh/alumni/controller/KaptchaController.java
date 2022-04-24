package cn.glh.alumni.controller;

import cn.glh.alumni.controller.LoginController;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/kaptcha")
public class KaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private Producer kaptchaProducer;

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 进入登录页面, 生成验证码, 并存入 Redis
     * @param response
     */
    @GetMapping("/get")
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
    public String checkKaptchaCode(String kaptchaOwner, String checkCode) {
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
}
