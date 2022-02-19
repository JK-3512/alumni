package cn.glh.alumni.interceptor;

import cn.glh.alumni.entity.LoginTicket;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.service.UserService;
import cn.glh.alumni.util.AlumniConstant;
import cn.glh.alumni.util.CookieUtil;
import cn.glh.alumni.util.HostHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2022/2/9 19:50
 * Description
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private HostHolder hostHolder;

    /**
     * 在 Controller 执行之前被调用
     * 检查凭证状态，若凭证有效则在本次请求中持有该用户信息
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证状态（是否有效）以及是否过期
            if (loginTicket != null && loginTicket.isStatus() && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.selectById(loginTicket.getUserId());
                // 在本次请求中持有用户信息
                hostHolder.setUser(user);
                // 构建用户认证的结果，并存入 SecurityContext, 以便于 Spring Security 进行授权
                ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(
                        user.getType() == 0 ? AlumniConstant.AUTHORITY_USER : AlumniConstant.AUTHORITY_ADMIN));
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPwd(), grantedAuthorities
                );
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    /**
     * 在模板引擎之前被调用
     * 将用户信息存入 modelAndView, 便于模板引擎调用
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在 Controller 执行之后（即服务端对本次请求做出响应后）被调用
     * 清理本次请求持有的用户信息
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
