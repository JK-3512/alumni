package cn.glh.alumni.service.impl;

import cn.glh.alumni.dao.UserEventLogDao;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.service.UserEventLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Administrator
 * @Date: 2022/2/10 18:31
 * Description
 */
@Service("userEventLogServiceImpl")
public class UserEventLogServiceImpl implements UserEventLogService {

    @Resource
    private UserEventLogDao userEventLogDao;

    @Override
    public void insertUserEventLog(UserEventLog userEventLog) {
        userEventLogDao.insertUserEventLog(userEventLog);
    }

}
