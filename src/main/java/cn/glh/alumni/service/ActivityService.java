package cn.glh.alumni.service;

import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.ActivityEnroll;
import cn.glh.alumni.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 活动表(Activity)表服务接口
 *
 * @author Administrator
 * @since 2022-02-08 10:20:43
 */
public interface ActivityService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Activity selectById(Integer id);

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    PageInfo<Activity> selectByPage(BasePage basePage);

    /**
     * 新增活动
     * @param activity
     */
    void insertActivity(Activity activity);

    /**
     * 修改数据
     *
     * @param activity 实例对象
     * @return 实例对象
     */
    int updateActivity(Activity activity);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Integer id);

    /**
     * 获取活动详细信息
     * @param id
     * @return map K-V形式
     */
    Activity getDetails(int id);

    /**
     * 活动报名
     * @param activityId 活动Id
     * @param userId 报名人Id
     * @return map
     */
    Map<String, Object> insertEnrollUser(Integer activityId, Integer userId);

    /**
     * 获取本次活动参与人
     * @param id
     * @return
     */
    List<ActivityEnroll> getEnrollUser(int id);

    /**
     * 进入到活动列表页面
     * @param sort 分类
     * @return 活动集合
     */
    List<Activity> getActivityList(Integer sort);
}
