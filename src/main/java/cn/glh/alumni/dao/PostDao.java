package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帖子表(Post)表数据库访问层
 *
 * @author makejava
 * @since 2022-02-28 09:28:22
 */
@Mapper
public interface PostDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Post selectById(Integer id);

    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<Post> queryAll();

    /**
     * 新增数据
     *
     * @param post 实例对象
     * @return 影响行数
     */
    int insertPost(Post post);

    /**
     * 修改数据
     *
     * @param post 实例对象
     * @return 影响行数
     */
    int updatePost(Post post);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 获取该分类下的所有帖子
     * @param sort 类别
     * @return 帖子集合
     */
    List<Post> queryBySort(String sort);

    /**
     * 找出用户发布的帖子
     * @param userId
     * @return
     */
    List<Post> findByUserId(Integer userId);
}

