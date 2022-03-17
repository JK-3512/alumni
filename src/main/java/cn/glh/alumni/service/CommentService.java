package cn.glh.alumni.service;

import cn.glh.alumni.entity.Comment;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.CommentDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.HostHolder;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 评论回复表(Comment)表服务实现类
 *
 * @author Administrator
 * @since 2022-02-14 09:45:41
 */
@Service
public class CommentService {

    @Resource
    private CommentDao commentDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    /**
     * 通过ID查询单条数据
     * @param id 主键
     * @return 实例对象
     */
    public Comment selectById(Integer id) {
        return this.commentDao.selectById(id);
    }

    /**
     * 分页查询
     * @param basePage 分页参数
     * @return 查询结果
     */
    public PageInfo<Comment> selectByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo(() -> commentDao.selectAll());
    }

    /**
     * 新增评论/回复
     * @param comment 实例对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        User user = hostHolder.getUser();
        //设置评论/回复的所在用户Id、状态、创建时间
        comment.setFromUserId(user.getId());
        comment.setState(false);
        comment.setCreateTime(new Date());
        commentDao.insertComment(comment);
    }

    /**
     * 修改数据
     * @param comment 实例对象
     * @return 实例对象
     */
    public int updateComment(Comment comment) {
        return this.commentDao.updateComment(comment);
    }

    /**
     * 通过主键删除数据
     * @param id 主键
     * @return 是否成功
     */
    public int deleteById(Integer id) {
        return this.commentDao.deleteById(id);
    }

    /**
     * 删除目标类型及目标ID下的评论回复
     * @param targetType 目标类型
     * @param targetId 目标ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Integer targetType, Integer targetId) {
        //目标的全部评论
        List<Comment> commentList = findCommentByType(targetType, targetId);
        for (Comment comment : commentList) {
            //清除该条评论的点赞
            likeService.deleteTargetLike(AlumniEnum.fromCode(targetType).getAlumniType(), comment.getId());
            //清理该条评论
            this.commentDao.deleteById(comment.getId());
            //评论的全部回复
            List<Comment> replyList = this.findCommentByType(AlumniEnum.comment.getCode(), comment.getId());
            for (Comment reply : replyList) {
                //清除回复的点赞
                likeService.deleteTargetLike(AlumniEnum.comment.getAlumniType(), reply.getId());
                //清理回复
                this.commentDao.deleteById(reply.getId());
            }
        }
    }

    /**
     * 找到业务类型、ID所属评论
     * @param type 业务类型
     * @param id 具体内容ID
     * @return 评论集合
     */
    public List<Comment> findCommentByType(int type, int id) {
        return commentDao.selectCommentByType(type, id);
    }

    /**
     * 回复专属、找到对应的评论
     * @param id
     * @return
     */
    public Comment findCommentById(Integer id) {
        return commentDao.findCommentById(id);
    }
}
