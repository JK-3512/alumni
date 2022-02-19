package cn.glh.alumni.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 配置类
 * @author Administrator
 */
@Configuration
public class RedisConfig {

    /**
     * 前台登录界面生成时发送请求从后台获取验证码图片
     * 后台生成验证码图片后，再生成uuid，把验证码对应的正确字符str和这个uuid进行KV映射存储到Redis中，返回给前台，顺便把验证码的uuid放到请求的Cookie中。
     * 用户登录：用户输入账号密码、验证码，随后带着Cookie中的uuid一起发送给后台。
     * 后台把redis中的字符值和前台传入的进行比较，如果可行，那么就继续比对账号密码，看是否正确。
     */

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置 key 的序列化的方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置 value 的序列化的方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置 hash 的 key 的序列化的方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置 hash 的 value 的序列化的方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;
    }

}
