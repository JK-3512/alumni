package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.LoginTicket;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.util.AlumniConstant;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.MailClient;
import cn.glh.alumni.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 校友表(User)表服务实现类
 *
 * @author Administrator
 * @since 2022-02-05 14:54:27
 */
@Service
public class LoginService {
    @Resource
    private UserDao userDao;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MailClient mailClient;

    // 网站域名
    @Value("${alumni.path.domain}")
    private String domain;

    // 项目名(访问路径)
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 通过ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    public User selectById(Integer id) {
        User user = getCache(id); // 优先从缓存中查询数据
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    /**
     * 发送邮件验证码(用于重置密码)
     * @param userName 用户名
     * @return Map<String, Object> 返回错误提示消息，如果返回的 map 为空，则说明发送验证码成功
     */
    public Map<String, Object> doSendEmailCode4ResetPwd(String userName) {
        Map<String, Object> map = new HashMap<>(2);
        User user = userDao.selectByName(userName);
        if (user == null) {
            map.put("errMsg", "未发现账号");
            return map;
        }
        final String email = user.getEmail();
        if (StringUtils.isBlank(email)) {
            map.put("errMsg", "该账号未绑定邮箱");
            return map;
        }

        // 生成6位验证码
        String randomCode = AlumniUtil.getRandomCode(6);
        // 给注册用户发送激活邮件
        Context context = new Context();
        context.setVariable("email", "您的验证码是 " + randomCode);
//        // http://localhost:8080/alumni/user/activation/用户id/激活码
//        String url = domain + contextPath + "/user/activation/" + user.getId() + "/" + user.getActivationCode();
//        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(email,"重置 Alumni 账号密码", content);

        final String redisKey = "EmailCode4ResetPwd:" + userName;

        redisTemplate.opsForValue().set(redisKey, randomCode, 600, TimeUnit.SECONDS);
        return map;
    }

    /**
     * 重置密码
     * @param userName 用户名
     * @param pwd 新密码
     * @return Map<String, Object> 返回错误提示消息，如果返回的 map 为空，则说明发送验证码成功
     */
    public Map<String, Object> doResetPwd(String userName, String pwd) {
        Map<String, Object> map = new HashMap<>(2);
        if (StringUtils.isBlank(pwd)) {
            map.put("errMsg", "密码不能为空");
            return map;
        }
        User user = userDao.selectByName(userName);
        if (user == null) {
            map.put("errMsg", "未发现账号");
            return map;
        }
        final String passwordEncode = AlumniUtil.md5(pwd + user.getSalt());
        int i = userDao.updatePwd(user.getId(), passwordEncode);
        if (i <= 0) {
            map.put("errMsg", "修改数据库密码错误");
        } else {
            clearCache(user.getId());
        }
        return map;
    }

    /**
     * 注册用户
     * @param user 用户
     * @return Map<String, Object> 返回错误提示消息，如果返回的 map 为空，则说明注册成功
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUserName())) {
            map.put("msg", "账号不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getPwd())) {
            map.put("msg", "密码不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("msg", "邮箱不能为空");
            return map;
        }

        // 验证账号是否已存在
        User u = userDao.selectByName(user.getUserName());
        if (u != null) {
            map.put("msg", "该账号已存在");
            return map;
        }

        // 验证邮箱是否已存在
        u = userDao.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("msg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        user.setSalt(AlumniUtil.generateUUID().substring(0, 5));
        user.setPwd(AlumniUtil.md5(user.getPwd() + user.getSalt()));
        user.setType(0);
        user.setState(false);
        user.setActivationCode(AlumniUtil.generateUUID());
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // 给注册用户发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/alumni/user/activation/用户id/激活码
        String url = domain + contextPath + "/user/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活宁理校友网账号", content);
        return map;
    }

    /**
     * 激活用户
     * @param userId 用户 id
     * @param code 激活码
     * @return 0-激活成功 1-已激活 2-激活失败
     */
    public int activation(int userId, String code) {
        User user = userDao.selectById(userId);
        if (user.getState()) {
            // 用户已激活
            return AlumniConstant.ACTIVATION_REPEAT;
        }
        else if (user.getActivationCode().equals(code)) {
            // 修改用户状态为已激活
            userDao.updateStatus(userId, 1);
            // 用户信息变更，清除缓存中的旧数据
            clearCache(userId);
            return AlumniConstant.ACTIVATION_SUCCESS;
        }
        else {
            return AlumniConstant.ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登录（为用户创建凭证）
     * @param userName 用户名
     * @param pwd 密码
     * @return Map<String, Object> 返回错误提示消息以及 ticket(凭证)
     */
    public Map<String, Object> login(String userName, String pwd) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(userName)) {
            map.put("msg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(pwd)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        // 验证账号
        User user = userDao.selectByName(userName);
        if (user == null) {
            map.put("msg", "该账号不存在");
            return map;
        }

        // 验证状态
        if (!user.getState()) {
            // 账号未激活
            map.put("msg", "该账号未激活");
            return map;
        }

        // 验证密码
        pwd = AlumniUtil.md5(pwd + user.getSalt());
        if (!user.getPwd().equals(pwd)) {
            map.put("msg", "密码错误");
            return map;
        }

        // 用户名和密码均正确，为该用户生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        // 随机凭证
        loginTicket.setTicket(AlumniUtil.generateUUID());
        // 设置凭证状态为有效（当用户登出的时候，设置凭证状态为无效）
        loginTicket.setStatus(true);
        // 设置凭证到期时间
        loginTicket.setExpired(new Date(System.currentTimeMillis() + AlumniConstant.DEFAULT_EXPIRED_SECONDS * 1000));

        // 将登录凭证存入 redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 根据 ticket 查询 LoginTicket 信息
     * @param ticket 凭证
     * @return LoginTicket 登录凭证
     */
    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 用户退出（将凭证状态设为无效/删除）
     * @param ticket 凭证
     */
    public void logout(String ticket) {
        // 修改（先删除再插入）对应用户在 redis 中的凭证状态
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
//        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//        loginTicket.setStatus(false);
//        redisTemplate.opsForValue().set(redisKey, loginTicket);
        redisTemplate.delete(redisKey);
    }

    /**
     * 优先从缓存中取值
     * @param userId 用户ID
     * @return 用户信息
     */
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 缓存中没有该用户信息时，则将其存入缓存
     * @param userId 用户ID
     * @return 用户信息
     */
    private User initCache(int userId) {
        User user = userDao.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 用户信息变更时清除对应缓存数据
     * @param userId 用户ID
     */
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}