package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.ActivityEnroll;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 活动表(Activity)表数据库访问层
 *
 * @author Administrator
 * @since 2022-02-08 10:20:43
 */
@Mapper
public interface ActivityDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Activity selectById(Integer id);

    /**
     * 通过用户名查询单条数据
     *
     * @param userName 用户名
     * @return 实例对象
     */
    Activity selectByName(String userName);

    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<Activity> selectAll();

    /**
     * 新增数据
     *
     * @param activity 实例对象
     * @return 影响行数
     */
    int insertActivity(Activity activity);

    /**
     * 修改数据
     *
     * @param activity 实例对象
     * @return 影响行数
     */
    int updateActivity(Activity activity);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 登记活动报名人
     * @param activityEnroll 实例对象
     * @return 影响行数
     */
    int insertEnrollUser(ActivityEnroll activityEnroll);

    /**
     * 获取本次活动参与人
     * @param id 活动ID
     * @return 报名人及报名日期
     */
    List<ActivityEnroll> getEnrollUser(Integer id);

    /**
     * 查找是否存在重复报名的情况
     * @param activityId
     * @param userId
     * @return
     */
    ActivityEnroll findEnrollUser(Integer activityId, Integer userId);

    /**
     * 找出所有活动
     * @return
     */
    List<Activity> findAllActivity();

    /**
     * 根据分类找出所属活动
     * @param sort
     * @return
     */
    List<Activity> findSortActivity(String sort);
}

