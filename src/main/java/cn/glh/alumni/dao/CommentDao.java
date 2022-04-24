package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Comment;
import cn.glh.alumni.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评论回复表(Comment)表数据库访问层
 *
 * @author Administrator
 * @since 2022-02-14 09:45:40
 */
@Mapper
public interface CommentDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment selectById(Integer id);

    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<Comment> selectAll();

    /**
     * 分页查询全部
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Comment> queryByPage(Integer offset, Integer rows);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int insertComment(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 影响行数
     */
    int updateComment(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 找到业务类型、ID所属评论
     * @param type 业务类型
     * @param id 具体内容ID
     * @return 评论集合
     */
    List<Comment> selectCommentByType(Integer type, Integer id);

    /**
     * 回复专属、找到对应的评论
     */
    Comment findCommentById(Integer id);
}

