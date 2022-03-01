package cn.glh.alumni.service;

import cn.glh.alumni.entity.Post;
import cn.glh.alumni.base.BasePage;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 帖子表(Post)表服务接口
 *
 * @author makejava
 * @since 2022-02-28 09:28:23
 */
public interface PostService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Post queryById(Integer id);

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    PageInfo<Post> queryByPage(BasePage basePage);

    /**
     * 新增数据
     *
     * @param post 实例对象
     * @return 实例对象
     */
    int insert(Post post);

    /**
     * 修改数据
     *
     * @param post 实例对象
     * @return 实例对象
     */
    int update(Post post);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Integer id);


    /**
     * 发布帖子
     * @param post
     */
    void insertPost(Post post);

    /**
     * 获取分类下的所有帖子
     * @param sort 分类
     * @return
     */
    List<Post> getPostList(Integer sort);
}
