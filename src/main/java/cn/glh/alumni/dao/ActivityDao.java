package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.ActivityEnroll;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    int deleteById(@Param("id") Integer id);

    /**
     * 登记活动报名人
     * @param activityEnroll 实例对象
     * @return 影响行数
     */
    int insertEnrollUser(ActivityEnroll activityEnroll);

    /**
     * 取消活动报名
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return
     */
    int cancelEnroll(@Param("activityId") Integer activityId, @Param("userId") Integer userId);

    /**
     * 获取本次活动参与人
     * @param id 活动ID
     * @return 报名人及报名日期
     */
    List<ActivityEnroll> getEnrollUser(@Param("id") Integer id);

    /**
     * 查找是否存在重复报名的情况
     * @param activityId
     * @param userId
     * @return
     */
    ActivityEnroll findEnrollUser(@Param("activityId") Integer activityId, @Param("userId") Integer userId);

    /**
     * 找出所有活动
     * @return
     */
    List<Activity> findAllActivity(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 根据分类找出所属活动
     * @param sort
     * @return
     */
    List<Activity> findSortActivity(@Param("sort") String sort, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 找出用户发布的活动
     * @param userId 用户ID
     * @return 活动集合
     */
    List<Activity> findByUserId(@Param("userId") Integer userId);

    /**
     * 清除活动报名表
     * @param activityId
     */
    void deleteEnrollUser(@Param("activityId") Integer activityId);

    /**
     * 分页查询已审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Activity> queryByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 分页查询未审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Activity> queryAuditByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    int updateState(@Param("id") Integer id, @Param("state") Integer state);

    /**
     * 找出用户参加的所有活动ID
     * @param userId
     * @return 活动ID集合
     */
    List<Integer> findEnrollByUserId(@Param("userId") Integer userId);

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    Integer getCountBySort(@Param("sort") String sort);

    /**
     * 获取总条码
     * @return
     */
    Integer getCount();

    /**
     * 活动检索
     * @param search 关键词
     * @return
     */
    List<Activity> searchActivity(@Param("title") String title, @Param("sort") String sort);
}

