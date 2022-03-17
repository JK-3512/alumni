package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.Post;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.PostDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.entity.enums.ActivityEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.entity.enums.PostEnum;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

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
public class PostService {
    @Resource
    private PostDao postDao;

    @Resource
    private UserDao userDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    /**
     * 通过ID查询单条数据
     * @param id 主键
     * @return 实例对象
     */
    public Post selectById(Integer id) {
        return this.postDao.selectById(id);
    }

    /**
     * 分页查询
     * @param basePage 分页参数
     * @return 查询结果
     */
    public PageInfo<Post> queryByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo(() -> postDao.queryAll());
    }

    /**
     * 新增数据
     * @param post 实例对象
     * @return 实例对象
     */
    public int insert(Post post) {
        return this.postDao.insertPost(post);
    }

    /**
     * 修改数据
     * @param post 实例对象
     * @return 实例对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Post post) {
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //重新设置帖子的类别、状态
        post.setSort(PostEnum.fromCode(Integer.valueOf(post.getSort())).getPostType());
        post.setState(0);
        this.postDao.updatePost(post);
    }

    /**
     * 通过主键删除数据
     * @param id 主键
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer id) {
        //清除reids缓存中该帖子的点赞数据
        likeService.deleteTargetLike(AlumniEnum.post.getAlumniType(), id);
        //清除reids缓存中该帖子的收藏数据
        collectService.deleteTargetCollect(AlumniEnum.post.getAlumniType(), id);
        //清除该帖子的评论及回复
        commentService.deleteComment(AlumniEnum.post.getCode(), id);

        //最后清除帖子
        return this.postDao.deleteById(id);
    }

    /**
     * 发布帖子
     * @param post 帖子对象
     */
    @Transactional(rollbackFor = Exception.class)
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
    }

    /**
     * 获取分类下的所有帖子
     * @param sort 分类
     * @return List<Post> 帖子集合
     */
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

    /**
     * 找出用户发布的帖子
     * @param userId
     * @return
     */
    public List<Post> findByUserId(Integer userId) {
        return postDao.findByUserId(userId);
    }

    /**
     * 获取用户收藏的所有帖子
     * @param postIds 帖子ID列表
     * @return
     */
    public List<Post> findCollectPost(List<Integer> postIds) {
        List<Post> postList = new ArrayList<>();
        for (Integer id : postIds) {
            Post post = postDao.selectById(id);
            //收藏的帖子为空，说明帖子已被删除，需要修改用户收藏数据
            if (post == null){
                collectService.deleteTargetCollector(AlumniEnum.post.getAlumniType(), id, hostHolder.getUser().getId());
            }else{
                post.setAuthor(userDao.selectById(post.getUserId()).getNickName());
                postList.add(post);
            }

        }
        return postList;
    }
}
