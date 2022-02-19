package cn.glh.alumni.service.impl;

import cn.glh.alumni.entity.Comment;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.CommentDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.service.CommentService;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
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
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentDao commentDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment selectById(Integer id) {
        return this.commentDao.selectById(id);
    }

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    @Override
    public PageInfo<Comment> selectByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo(() -> commentDao.selectAll());
    }

    /**
     * 新增评论/回复
     *
     * @param comment 实例对象
     */
    @Override
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

        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(), user.getUserName() + "发布了评论/回复:"+ comment.getContent(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
    }

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public int updateComment(Comment comment) {
        return this.commentDao.updateComment(comment);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Integer id) {
        return this.commentDao.deleteById(id);
    }

    /**
     * 找到业务类型、ID所属评论
     * @param type 业务类型
     * @param id 具体内容ID
     * @return 评论集合
     */
    @Override
    public List<Comment> findCommentByType(int type, int id) {
        return commentDao.selectCommentByType(type, id);
    }

    /**
     * 回复专属、找到对应的评论
     * @param id
     * @return
     */
    @Override
    public Comment findCommentById(Integer id) {
        return commentDao.findCommentById(id);
    }
}
