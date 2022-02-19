package cn.glh.alumni.service;

import cn.glh.alumni.entity.Comment;
import cn.glh.alumni.base.BasePage;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 评论回复表(Comment)表服务接口
 *
 * @author Administrator
 * @since 2022-02-14 09:45:41
 */
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment selectById(Integer id);

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    PageInfo<Comment> selectByPage(BasePage basePage);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     */
    void insertComment(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    int updateComment(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Integer id);

    /**
     * 找到业务类型、ID所属评论
     * @param type 业务类型
     * @param id 具体内容ID
     * @return 评论集合
     */
    List<Comment> findCommentByType(int type, int id);

    /**
     * 回复专属、找到对应的评论
     * @param id
     * @return
     */
    Comment findCommentById(Integer id);
}