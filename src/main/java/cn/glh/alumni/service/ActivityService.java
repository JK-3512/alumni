package cn.glh.alumni.service;

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
public class ActivityService {
    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserDao userDao;
    @Resource
    private ActivityDao activityDao;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;



    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    public Activity selectById(Integer id) {
        return this.activityDao.selectById(id);
    }

    /**
     * 新增活动（默认当前用户报名）
     * 需要做空值处理
     * @param activity 实例对象
     */
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
        activity.setState(0);

        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(),  "发布了活动:"+ activity.getTitle(), new Date());
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
     */
    public int updateActivity(Activity activity) {
        if (activity == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //重新设置活动的类别、状态
        activity.setSort(ActivityEnum.fromCode(Integer.valueOf(activity.getSort())).getActivitySort());
        activity.setState(0);

        int i = this.activityDao.updateActivity(activity);

        User user = hostHolder.getUser();
        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(),  "更新了活动:"+ activity.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
        return i;

    }

    /**
     * 通过主键删除数据,删除活动对应下的评论回复及点赞、收藏状态
     * @param id 主键
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer id) {
        //生成动态
        Activity activity = this.selectById(id);
        //事件产生
        UserEventLog userEventLog = new UserEventLog(hostHolder.getUser().getId(),  "删除了活动:"+ activity.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));

        //清除reids缓存中该活动的点赞数据
        likeService.deleteTargetLike(AlumniEnum.activity.getAlumniType(), id);
        //清除reids缓存中该活动的收藏数据
        collectService.deleteTargetCollect(AlumniEnum.activity.getAlumniType(), id);
        //清除活动的报名表(外键约束)
        this.deleteEnrollUser(id);
        //清除该活动的评论及回复
        commentService.deleteComment(AlumniEnum.activity.getCode(), id);

        //最后清除活动
        return this.activityDao.deleteById(id);
    }

    /**
     * 活动报名
     * @param activityId 活动Id
     * @param userId 报名人Id
     */
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
     * 取消活动报名
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return
     */
    public int cancelEnroll(Integer activityId, Integer userId) {
        return activityDao.cancelEnroll(activityId, userId);
    }

    /**
     * 获取本次活动参与人
     * @param id 活动Id
     * @return List<ActivityEnroll> 报名人集合
     */
    public List<ActivityEnroll> getEnrollUser(int id){
        List<ActivityEnroll> enrollList = activityDao.getEnrollUser(id);
        //遍历集合，通过userId获取活动参与人及头像
        for (ActivityEnroll activityEnroll : enrollList) {
            User user = userDao.selectById(activityEnroll.getUserId());
            activityEnroll.setNickName(user.getNickName());
            activityEnroll.setHeadPic(user.getHeadPic());
        }
        return enrollList;
    }

    public void deleteEnrollUser(Integer activityId){
        this.activityDao.deleteEnrollUser(activityId);
    }

    /**
     * 进入到活动列表页面
     * @param sort 分类
     * @param page 分页参数(当前页码)
     * @param limit 分页参数(数据量)
     * @return 活动集合
     */
    public List<Activity> getActivityList(Integer sort, Integer page, Integer limit) {
        List<Activity> activityList = null;
        //全部查询
        if (sort == ActivityEnum.all.getCode()){
            activityList  = activityDao.findAllActivity((page - 1) * limit, limit);
        }else{
            activityList = activityDao.findSortActivity(ActivityEnum.fromCode(sort).getActivitySort(), (page - 1) * limit, limit);
        }
        return activityList;
    }


    /**
     * 找出用户发布的活动
     * @param userId
     * @return
     */
    public List<Activity> findByUserId(Integer userId) {
        return activityDao.findByUserId(userId);
    }

    /**
     * 找出用户参加的所有活动
     * @param userId
     */
    public List<Activity> findEnrollByUserId(Integer userId) {
        List<Integer> ids = activityDao.findEnrollByUserId(userId);
        List<Activity> activityList = new ArrayList<>();
        for (Integer id : ids) {
            Activity activity = activityDao.selectById(id);
            activity.setAuthor(userDao.selectById(activity.getUserId()).getNickName());
            activityList.add(activity);
        }
        return activityList;
    }

    /**
     * 获取用户收藏的所有活动
     * @param activityIds 活动ID列表
     * @return
     */
    public List<Activity> findCollectActivity(List<Integer> activityIds) {
        List<Activity> activityList = new ArrayList<>();
        for (Integer id : activityIds) {
            Activity activity = activityDao.selectById(id);
            //收藏的活动为空，说明活动已被删除，则应当清除出用户的收藏列表中
            if (activity == null){
                collectService.deleteTargetCollector(AlumniEnum.activity.getAlumniType(), id, hostHolder.getUser().getId());
            }else{
                activity.setAuthor(userDao.selectById(activity.getUserId()).getNickName());
                activityList.add(activity);
            }

        }
        return activityList;
    }

    /**
     * 分页查询已审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return
     */
    public List<Activity> queryByPage(Integer page, Integer limit) {
        return activityDao.queryByPage((page - 1) * limit, limit);
    }

    /**
     * 分页查询未审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return
     */
    public List<Activity> queryAuditByPage(Integer page, Integer limit) {
        return activityDao.queryAuditByPage((page - 1) * limit, limit);
    }

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    public int updateState(Integer id, Integer state){
        return activityDao.updateState(id, state);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStateList(List<Integer> ids, Integer state){
        for (Integer id : ids) {
            this.updateState(id, state);
        }
    }

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    public Integer getCountBySort(Integer sort) {
        if (sort == ActivityEnum.all.getCode()){
            return activityDao.getCount();
        }
        return activityDao.getCountBySort(ActivityEnum.fromCode(sort).getActivitySort());
    }

    /**
     * 活动检索
     * @param search 关键词
     * @return
     */
    public List<Activity> searchActivity(String search) {
        return activityDao.searchActivity(search);
    }
}
