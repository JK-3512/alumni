package cn.glh.alumni.util;

import cn.glh.alumni.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Author: Administrator
 * @Date: 2022/2/9 19:51
 * Description 持有用户信息(多线程)，用于代替 session 对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    // 存储 User
    public void setUser(User user) {
        users.set(user);
    }

    // 获取 User
    public User getUser() {
        return users.get();
    }

    // 清理
    public void clear() {
        users.remove();
    }

}