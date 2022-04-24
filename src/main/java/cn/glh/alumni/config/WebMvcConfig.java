package cn.glh.alumni.config;

import cn.glh.alumni.interceptor.LoginTicketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author: Administrator
 * @Date: 2022/2/4 13:32
 * Description 拦截器配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;

    /**
     * 配置静态访问资源
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.json","/**/*.svg","/**/*.png","/**/*.jpg","/**/*.gif","/**/font/*");
    }
}
