package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.*;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.entity.enums.PostEnum;
import cn.glh.alumni.service.*;
import cn.glh.alumni.util.HostHolder;
import com.github.pagehelper.PageInfo;
import cn.glh.alumni.base.*;
import cn.glh.alumni.service.impl.PostServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 帖子表(Post)表控制层
 *
 * @author makejava
 * @since 2022-02-28 09:28:22
 */
@Controller
@RequestMapping("/user/post")
public class PostController {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;
    /**
     * 服务对象
     */
    @Resource
    private PostService postService;

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return Result对象
     */
    @PostMapping(value = "/queryPage")
    public Result<PageInfo<Post>> queryPage(@RequestBody BasePage basePage) {
        PageInfo<Post> page = postService.queryByPage(basePage);
        return Result.ok(page);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping(value = "/get/{id}")
    public Result<Post> queryById(@PathVariable("id") Integer id) {
        Post result = postService.queryById(id);
        if (Objects.nonNull(result)) {
            return Result.ok(result);
        }
        return Result.fail(0, "查询失败");
    }

    /**
     * 新增数据
     *
     * @param post 实体
     * @return 新增结果
     */
    @PostMapping(value = "/insert")
    public Result insert(@RequestBody Post post) {
        int result = postService.insert(post);
        if (result > 0) {
            return Result.ok();
        }
        return Result.fail(0, "新增失败");
    }

    /**
     * 编辑数据
     *
     * @param post 实体
     * @return 编辑结果
     */
    @PutMapping(value = "/update")
    public Result update(@RequestBody Post post) {
        int result = postService.update(post);
        if (result > 0) {
            return Result.ok();
        }
        return Result.fail(0, "修改失败");
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        int result = postService.deleteById(id);
        if (result > 0) {
            return Result.ok();
        }
        return Result.fail(0, "删除失败");
    }



    /**
     * 进入到帖子列表页
     * @param model
     * @return
     */
    @GetMapping("/list/{sort}")
    public String getPostList(Model model, @PathVariable(value = "sort") Integer sort){
        //获取给分类下的所有帖子
        List<Post> postList = postService.getPostList(sort);
        //用来存储所有帖子所属的所有点赞、收藏、评论、回复
        List<Map<String, Object>> postVoList = new ArrayList<>();
        //遍历集合，获取单独的帖子
        if (postList != null){
            //为每一个帖子计算留言数
            int msgNum = 0;
            for (Post post : postList) {
                //存储单个帖子的点赞、收藏、评论、回复
                Map<String, Object> postVo = new HashMap<>();
                //先加入帖子本体
                postVo.put("post", post);
                // 帖子的点赞数量
                long likeCount = likeService.findTargetLikeCount(AlumniEnum.post.getAlumniType(), post.getId());
                // 当前登录用户的点赞状态
                int likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findTargetLikeStatus(AlumniEnum.post.getAlumniType(), post.getId(), hostHolder.getUser().getId());
                postVo.put("likeCount", likeCount);
                postVo.put("likeStatus", likeStatus);

                //帖子收藏数量
                long collectCount = collectService.findTargetCollectCount(AlumniEnum.post.getAlumniType(), post.getId());
                //当前用户的收藏状态
                int collectStatus = hostHolder.getUser() == null ? 0 :
                        collectService.findTargetCollectStatus(AlumniEnum.post.getAlumniType(), post.getId(), hostHolder.getUser().getId());
                postVo.put("collectCount", collectCount);
                postVo.put("collectStatus", collectStatus);

                //遍历评论集合获取每个评论的用户名、头像、内容、时间
                List<Comment> commentList = commentService.findCommentByType(AlumniEnum.post.getCode(), post.getId());
                List<Map<String, Object>> commentVoList = new ArrayList<>();

                if (commentList != null){
                    msgNum += commentList.size();
                    for (Comment comment : commentList) {
                        //存储对活动的评论
                        Map<String, Object> commentVo = new HashMap<>();
                        //评论
                        commentVo.put("comment", comment);
                        //评论人
                        commentVo.put("user", userService.selectById(comment.getFromUserId()));
                        // 该评论点赞数量
                        likeCount = likeService.findTargetLikeCount(AlumniEnum.comment.getAlumniType(), comment.getId());
                        commentVo.put("likeCount", likeCount);
                        // 当前登录用户对该评论的点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findTargetLikeStatus(
                                AlumniEnum.comment.getAlumniType(), comment.getId(), hostHolder.getUser().getId());
                        commentVo.put("likeStatus", likeStatus);

                        //每个对评论的回复
                        List<Comment> replyList = commentService.findCommentByType(AlumniEnum.comment.getCode(), comment.getId());
                        // 封装对评论的评论和评论的作者信息
                        List<Map<String, Object>> replyVoList = new ArrayList<>();

                        if (replyList != null){
                            //每个评论的回复数
                            msgNum += replyList.size();
                            for (Comment reply : replyList) {
                                Map<String, Object> replyVo = new HashMap<>();
                                //回复
                                replyVo.put("reply", reply);
                                //回复人
                                replyVo.put("user", userService.selectById(reply.getFromUserId()));
                                // 该回复的点赞数量
                                likeCount = likeService.findTargetLikeCount(AlumniEnum.comment.getAlumniType(), reply.getId());
                                replyVo.put("likeCount", likeCount);
                                // 当前登录用户的点赞状态
                                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findTargetLikeStatus(
                                        AlumniEnum.comment.getAlumniType(), reply.getId(), hostHolder.getUser().getId());
                                replyVo.put("likeStatus", likeStatus);
                                replyVoList.add(replyVo);
                            }
                        }
                        //向评论中填充回复
                        commentVo.put("replys", replyVoList);
                        commentVoList.add(commentVo);
                    }
                }

                //为每一个帖子存储相应的评论、回复
                postVo.put("comments", commentVoList);
                postVo.put("msgNum", msgNum);
                postVoList.add(postVo);
            }
        }
        //所有帖子的全部内容
        model.addAttribute("posts", postVoList);
        return "post/list";
    }

    /**
     * 进入到帖子发布页面
     * @return String
     */
    @GetMapping("/publish")
    public String getPublishPage(Model model){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
            return "operate-result";
        }
        return "post/publish";
    }

    /**
     * 发布帖子
     * @param post
     * @return
     */
    @PostMapping("/add")
    public String addPost(Model model, Post post){
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            postService.insertPost(post);
            model.addAttribute("msg", "帖子发布成功!");
            model.addAttribute("target", "/user/post/list");
        }
        return "operate-result";
    }

}

