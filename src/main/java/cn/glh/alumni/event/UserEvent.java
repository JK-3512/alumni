package cn.glh.alumni.event;

import cn.glh.alumni.entity.UserEventLog;
import org.springframework.context.ApplicationEvent;
/**
 * @Author: Administrator
 * @Date: 2022/2/10 15:31
 * Description 创建监听器事件
 */
public class UserEvent extends ApplicationEvent {

    private final UserEventLog userEventLog;

    public UserEvent(UserEventLog userEventLog) {
        super(userEventLog);
        this.userEventLog = userEventLog;
    }

    public UserEventLog getUserEventLog(){return userEventLog;}
}
