package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserEventLogDao;
import cn.glh.alumni.entity.UserEventLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2022/2/10 18:31
 * Description
 */
@Service
public class UserEventLogService {

    @Resource
    private UserEventLogDao userEventLogDao;

    public List<UserEventLog> findByUserId(Integer userId){
        return userEventLogDao.findByUserId(userId);
    }

    public void insertUserEventLog(UserEventLog userEventLog) {
        userEventLogDao.insertUserEventLog(userEventLog);
    }

}
