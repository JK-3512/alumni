package cn.glh.alumni.service.impl;

import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.ActivityDao;
import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.ActivityEnroll;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.entity.enums.ActivityEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.service.ActivityService;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 活动表(Activity)表服务实现类
 *
 * @author Administrator
 * @since 2022-02-08 10:20:43
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    @Resource
    private ActivityDao activityDao;

    @Resource
    private UserDao userDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Activity selectById(Integer id) {
        return this.activityDao.selectById(id);
    }

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    @Override
    public PageInfo<Activity> selectByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo(() -> activityDao.selectAll());
    }

    /**
     * 新增活动（默认当前用户报名）
     * 需要做空值处理
     * @param activity 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertActivity(Activity activity) {
        if (activity == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        User user = hostHolder.getUser();
        //设置用户ID、类别、日期、点赞、收藏、状态
        activity.setUserId(user.getId());
        activity.setSort(ActivityEnum.fromCode(Integer.valueOf(activity.getSort())).getActivitySort());
        activity.setCreateTime(new Date());
        activity.setStar(0);
        activity.setCollect(0);
        activity.setState(0);

        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(), user.getUserName() + "发布了活动:"+ activity.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
        //先创建活动、再报名活动
        activityDao.insertActivity(activity);
        this.insertEnrollUser(activity.getId(), user.getId());
    }

    /**
     * 修改数据
     *
     * @param activity 实例对象
     * @return 实例对象
     */
    @Override
    public int updateActivity(Activity activity) {
        return this.activityDao.updateActivity(activity);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Integer id) { return this.activityDao.deleteById(id); }

    /**
     * 获取活动详细信息
     * @param id 活动ID
     * @return map K-V形式
     */
    @Override
    public Activity getDetails(int id) {
        return this.selectById(id);
    }

    /**
     * 活动报名
     * @param activityId 活动Id
     * @param userId 报名人Id
     */
    @Override
    public Map<String, Object> insertEnrollUser(Integer activityId, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        ActivityEnroll enrollUser = activityDao.findEnrollUser(activityId, userId);
        if (enrollUser != null){
            map.put("msg", "您已经报名过了，请不要重复报名!");
        }else{
            List<ActivityEnroll> enrollUserList = activityDao.getEnrollUser(activityId);
            Activity activity = activityDao.selectById(activityId);
            //创建活动时报名人集合为空 || 防止报名人数超出上限
            if (enrollUserList.isEmpty() || enrollUserList.size() < activity.getNumberLimit()){
                //设置活动Id、报名人Id、报名时间
                ActivityEnroll activityEnroll = new ActivityEnroll();
                activityEnroll.setActivityId(activityId);
                activityEnroll.setUserId(userId);
                activityEnroll.setEnrollTime(new Date());
                activityDao.insertEnrollUser(activityEnroll);
            }else{
                map.put("msg","很抱歉，活动的报名人数已满!");
            }
        }
        return map;
    }

    /**
     * 获取本次活动参与人
     * @param id
     * @return
     */
    @Override
    public List<ActivityEnroll> getEnrollUser(int id){
        List<ActivityEnroll> enrollList = activityDao.getEnrollUser(id);
        //遍历集合，通过userId获取活动参与人及头像
        for (ActivityEnroll activityEnroll : enrollList) {
            User user = userDao.selectById(activityEnroll.getUserId());
            activityEnroll.setUserName(user.getUserName());
            activityEnroll.setHeadPic(user.getHeadPic());
        }
        return enrollList;
    }

    /**
     * 进入到活动列表页面
     * @param sort 分类
     * @return 活动集合
     */
    @Override
    public List<Activity> getActivityList(Integer sort) {
        List<Activity> activityList = null;
        //全部查询
        if (sort == ActivityEnum.all.getCode()){
            activityList  = activityDao.findAllActivity();
        }else{
            activityList = activityDao.findSortActivity(ActivityEnum.fromCode(sort).getActivitySort());
        }
        return activityList;
    }


}
