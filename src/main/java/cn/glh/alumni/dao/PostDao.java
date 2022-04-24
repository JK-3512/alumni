package cn.glh.alumni.dao;

import cn.glh.alumni.entity.News;
import cn.glh.alumni.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<Post> findAllPost(@Param("offset") Integer offset, @Param("rows") Integer rows);

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
    List<Post> findSortPost(@Param("sort") String sort, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 找出用户发布的帖子
     * @param userId 用户ID
     * @return 帖子集合
     */
    List<Post> findByUserId(Integer userId);

    /**
     * 分页查询已审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Post> queryByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 分页查询未审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Post> queryAuditByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    int updateState(@Param("id") Integer id, @Param("state") Integer state);

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
     * 帖子检索
     * @param search 关键词
     * @return
     */
    List<Post> searchPost(@Param("search") String search);
}

