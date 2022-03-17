package cn.glh.alumni.dao;

import cn.glh.alumni.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 校友表(User)表数据库访问层
 *
 * @author Administrator
 * @since 2022-02-05 16:23:00
 */
@Mapper
public interface UserDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User selectById(Integer id);

    /**
     * 通过用户名查询单条数据
     *
     * @param userName 用户名
     * @return 实例对象
     */
    User selectByName(String userName);

    /**
     * 通过邮箱查询
     * @param email 邮箱
     * @return 实例对象
     */
    User selectByEmail(String email);

    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<User> queryAll();

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int insertUser(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int updateUser(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 修改用户状态
     * @param id 用户ID
     * @param state 0：未激活，1：已激活
     * @return 影响行数
     */
    int updateStatus(Integer id, Integer state);

    /**
     * 重置密码
     * @param id 用户ID
     * @param pwd 新密码
     * @return 影响行数
     */
    int updatePwd(Integer id, String pwd);
}

