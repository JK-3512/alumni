package cn.glh.alumni.dao;

import cn.glh.alumni.entity.UserEventLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户动态表(UserEventLog)表数据库访问层
 *
 * @author Administrator
 * @since 2022-02-10 18:32:41
 */
@Mapper
public interface UserEventLogDao {

    /**
     * 通过用户ID查询用户所有动态
     *
     * @param id 主键
     * @return 动态集合
     */
    List<UserEventLog> selectById(Integer id);

    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<UserEventLog> selectAll();

    /**
     * 新增数据
     *
     * @param userEventLog 实例对象
     * @return 影响行数
     */
    int insertUserEventLog(UserEventLog userEventLog);


    /**
     * 找到用户的动态
     * @param userId 用户ID
     * @return 动态列表
     */
    List<UserEventLog> findByUserId(Integer userId);
}

