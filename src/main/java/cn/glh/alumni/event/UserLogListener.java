package cn.glh.alumni.event;

import cn.glh.alumni.service.UserEventLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: Administrator
 * @Date: 2022/2/10 15:32
 * Description 事件监听器
 */
@Component
public class UserLogListener implements ApplicationListener<UserEvent> {

    @Resource
    private UserEventLogService userEventLogService;

    @Override
    public void onApplicationEvent(UserEvent userEvent) {
        userEventLogService.insertUserEventLog(userEvent.getUserEventLog());
    }
}
