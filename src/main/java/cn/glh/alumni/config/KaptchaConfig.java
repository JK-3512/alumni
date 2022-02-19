package cn.glh.alumni.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: Administrator
 * @Date: 2022/2/4 16:30
 * Description
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer KaptchaProducer() {
        Properties kaptchaProperties = new Properties();
        //图片边框
        kaptchaProperties.put("kaptcha.border", "no");
        //文字长度
        kaptchaProperties.put("kaptcha.textproducer.char.length","4");
        //图片高度
        kaptchaProperties.put("kaptcha.image.height","40");
        //图片高度
        kaptchaProperties.put("kaptcha.image.width","120");
        //图片样式:阴影
        kaptchaProperties.put("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.ShadowGimpy");
        //文字颜色
        kaptchaProperties.put("kaptcha.textproducer.font.color","black");
        //文字大小
        kaptchaProperties.put("kaptcha.textproducer.font.size","30");
        //干扰实现类
        kaptchaProperties.put("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        //文本集合，验证码值从此集合中获取
        kaptchaProperties.put("kaptcha.textproducer.char.string","acdefhkmnprtwxy2345678");

        Config config = new Config(kaptchaProperties);
        return config.getProducerImpl();
    }
}
