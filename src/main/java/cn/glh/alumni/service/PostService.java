package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.*;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.PostDao;
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
        Post post = this.postDao.selectById(id);
        post.setAuthor(userDao.selectById(post.getUserId()).getNickName());
        return post;
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
    public int updatePost(Post post) {
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //重新设置帖子的类别、状态
        post.setSort(PostEnum.fromCode(Integer.valueOf(post.getSort())).getPostType());
        post.setState(0);
        return this.postDao.updatePost(post);
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
    public int insertPost(Post post) {
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
        return postDao.insertPost(post);
    }

    /**
     * 获取分类下的所有帖子
     * @param sort 分类
     * @param page 分页参数(当前页码)
     * @param limit 分页参数(数据量)
     * @return List<Post> 帖子集合
     */
    public List<Post> getPostList(Integer sort, Integer page, Integer limit) {
        List<Post> postList = null;
        //所有帖子
        if (sort == PostEnum.all.getCode()){
            postList = postDao.findAllPost((page - 1) * limit, limit);
        }else {
            postList = postDao.findSortPost(PostEnum.fromCode(sort).getPostType(), (page - 1) * limit, limit);
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

    /**
     * 分页查询已审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return 新闻集合
     */
    public List<Post> queryByPage(Integer page, Integer limit) {
        return postDao.queryByPage((page - 1) * limit ,limit);
    }

    /**
     * 分页查询未审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return 新闻集合
     */
    public List<Post> queryAuditByPage(Integer page, Integer limit) {
        return postDao.queryAuditByPage((page - 1) * limit ,limit);
    }

    /**
     * 修改审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    public int updateState(Integer id, Integer state){
        return postDao.updateState(id, state);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStateList(List<Integer> ids, Integer state){
        for (Integer id : ids) {
            this.updateState(id, state);
        }
    }

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    public Integer getCountBySort(Integer sort) {
        if (sort == PostEnum.all.getCode()){
            return postDao.getCount();
        }
        return postDao.getCountBySort(PostEnum.fromCode(sort).getPostType());
    }

    /**
     * 帖子检索
     * @param search 关键词
     * @return
     */
    public List<Post> searchPost(String search) {
        return postDao.searchPost(search);
    }
}
