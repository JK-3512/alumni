package cn.glh.alumni.service;

import cn.glh.alumni.entity.LoginTicket;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.base.BasePage;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * 校友表(User)表服务接口
 *
 * @author Administrator
 * @since 2022-02-05 14:54:27
 */
public interface UserService {


    /**
     * 用户注册
     * @param user
     * @return  Map<String, Object> 返回错误提示消息，如果返回的 map 为空，则说明注册成功
     */
    Map<String, Object> register(User user) ;

    /**
     * 激活用户
     * @param userId 用户 id
     * @param code 激活码
     * @return
     */
    int activation(int userId, String code);

    /**
     * 用户登录（为用户创建凭证）
     * @param userName
     * @param pwd
     * @return Map<String, Object> 返回错误提示消息以及 ticket(凭证)
     */
    Map<String, Object> login(String userName, String pwd) ;

    /**
     * 根据 ticket 查询 LoginTicket 信息
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 用户退出（将凭证状态设为无效）
     * @param ticket
     */
    void logout(String ticket);

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User selectById(Integer id);

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    PageInfo<User> queryByPage(BasePage basePage);

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    int insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    int update(User user);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Integer id);



}
