package cn.glh.alumni;

import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.RedisKeyUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
@SpringBootTest
public class AlumniApplicationTests {

    @Test
    public void test(){
        String s = AlumniUtil.md5("123456" + "0246e");
        System.out.println(s);
    }

}
