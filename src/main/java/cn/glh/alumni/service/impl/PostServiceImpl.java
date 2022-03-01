package cn.glh.alumni.service.impl;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.Post;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.PostDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.entity.enums.PostEnum;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.service.PostService;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帖子表(Post)表服务实现类
 *
 * @author makejava
 * @since 2022-02-28 09:28:23
 */
@Service
public class PostServiceImpl implements PostService {
    @Resource
    private PostDao postDao;

    @Resource
    private UserDao userDao;

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
    public Post queryById(Integer id) {
        return this.postDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    @Override
    public PageInfo<Post> queryByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo(() -> postDao.queryAll());
    }

    /**
     * 新增数据
     *
     * @param post 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(Post post) {
        return this.postDao.insertPost(post);
    }

    /**
     * 修改数据
     *
     * @param post 实例对象
     * @return 实例对象
     */
    @Override
    public int update(Post post) {
        return this.postDao.update(post);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Integer id) {
        return this.postDao.deleteById(id);
    }

    /**
     * 发布帖子
     * @param post
     */
    @Override
    public void insertPost(Post post) {
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        User user = hostHolder.getUser();
        //设置帖子的分类、创建时间、状态
        post.setSort(PostEnum.fromCode(Integer.valueOf(post.getSort())).getPostType());
        post.setCreateTime(new Date());
        post.setState(0);
        post.setUserId(user.getId());
        //发布帖子
        postDao.insertPost(post);
        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(), user.getUserName() + "发布了帖子", new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
    }

    /**
     * 获取分类下的所有帖子
     * @param sort 分类
     * @return
     */
    @Override
    public List<Post> getPostList(Integer sort) {
        List<Post> postList = null;
        //所有帖子
        if (sort == PostEnum.all.getCode()){
            postList = postDao.queryAll();
        }else {
            postList = postDao.queryBySort(PostEnum.fromCode(sort).getPostType());
        }
        return postList;
    }


}
