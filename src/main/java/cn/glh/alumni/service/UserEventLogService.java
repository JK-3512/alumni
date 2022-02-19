package cn.glh.alumni.service;

import cn.glh.alumni.entity.UserEventLog;
import com.github.pagehelper.PageInfo;

/**
 * @Author: Administrator
 * @Date: 2022/2/10 15:34
 * Description
 */
public interface UserEventLogService {

    void insertUserEventLog(UserEventLog userEventLog);

//    PageInfo<UserEventLog> page(UserEventLogPageDTO userEventLogPageDTO);
}
