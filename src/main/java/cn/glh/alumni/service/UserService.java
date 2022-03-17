package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.enums.EducationEnum;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.HostHolder;
import cn.glh.alumni.util.RedisKeyUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private RedisTemplate redisTemplate;

    public User selectById(Integer id){
        return userDao.selectById(id);
    }

    /**
     * 修改个人资料
     * @param user 用户信息
     */
    public void updateProfile(User user){
        // 用户信息变更，清除缓存中的旧数据
        clearCache(user.getId());
        //判空
        if (!StringUtil.isNullOrEmpty(user.getEducation())){
            user.setEducation(EducationEnum.fromCode(Integer.valueOf(user.getEducation())).getEducationType());
        }
        userDao.updateUser(user);
    }

    /**
     * 修改密码
     * @param oldPwd 旧密码
     * @param pwd 新密码
     * @return map存放错误信息，没有则执行成功
     */
    public Map<String, Object> updatePassword(String oldPwd, String pwd) {
        Map<String, Object> map = new HashMap<>();
        User user = hostHolder.getUser();
        //重复密码
        if (oldPwd.equals(pwd)){
            map.put("msg", "重复密码,请重新输入");
            return map;
        }

        // 验证密码
        oldPwd = AlumniUtil.md5(oldPwd + user.getSalt());
        if (!user.getPwd().equals(oldPwd)) {
            map.put("msg", "原密码错误,请重新输入");
            return map;
        }

        int i = userDao.updatePwd(user.getId(), AlumniUtil.md5(pwd + user.getSalt()));
        if (i <= 0) {
            map.put("msg", "修改数据库密码错误");
            return map;
        }else{
            clearCache(user.getId());
        }
        return map;
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
