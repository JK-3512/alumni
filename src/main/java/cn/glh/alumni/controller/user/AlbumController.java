package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.*;
import cn.glh.alumni.entity.enums.AlbumEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.*;
import cn.glh.alumni.util.HostHolder;
import com.github.pagehelper.PageInfo;
import cn.glh.alumni.base.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;

/**
 * 相册表(Album)表控制层
 *
 * @author makejava
 * @since 2022-02-20 17:21:00
 */
@Controller
@RequestMapping("/user/album")
public class AlbumController {
    /**
     * 服务对象
     */
    @Resource
    private AlbumService albumService;

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
     * 分页查询
     *
     * @param basePage 分页参数
     * @return Result对象
     */
    @PostMapping(value = "/queryPage")
    public Result<PageInfo<Album>> queryPage(@RequestBody BasePage basePage) {
        PageInfo<Album> page = albumService.queryByPage(basePage);
        return Result.ok(page);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping(value = "/get/{id}")
    public Result<Album> queryById(@PathVariable("id") Integer id) {
    Album result = albumService.selectById(id);
        if(Objects.nonNull(result)){
            return Result.ok(result);
        }
        return Result.fail(0,"查询失败");
    }

    /**
     * 新增数据
     *
     * @param album 实体
     * @return 新增结果
     */
    @PostMapping(value = "/insert")
    public Result insert(@RequestBody Album album) {
        int result = albumService.insert(album);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"新增失败");
    }

    /**
     * 编辑数据
     *
     * @param album 实体
     * @return 编辑结果
     */
    @PutMapping(value = "/update")
    public Result update(@RequestBody Album album) {
        int result = albumService.update(album);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"修改失败");
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        int result = albumService.deleteById(id);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"删除失败");
    }

    /**
     * 进入到相册列表页面
     * @return String
     */
    @GetMapping("/list/{sort}")
    public String getAlbumList(Model model, @PathVariable(value = "sort") Integer sort){
        List<Album> albumList = albumService.getAlbumList(sort);
        model.addAttribute("albumList", albumList);
        return "album/list";
    }

    /**
     * 进入到发布相册页面
     * @param model
     * @return
     */
    @GetMapping("/publish")
    public String getPublish(Model model){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
            return "operate-result";
        }
        return "album/publish";
    }

    /**
     * 添加相册
     * @param model
     * @param album
     * @return
     */
    @PostMapping("/add")
    public String addAlbum(Model model, Album album){
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            albumService.insertAlbum(album);
            model.addAttribute("msg", "相册发布成功!");
            model.addAttribute("target", "/user/album/list");
        }
        return "operate-result";
    }

    /**
     * 进入到相册详情页
     * @return String
     */
    @GetMapping("/details/{id}")
    public String getDetails(Model model, @PathVariable("id") int id) {
        Album album = albumService.selectById(id);
        //获取相册下属所有的图片
        List<String> albumPicList = albumService.getAlbumPicList(id);
        album.setAlbumPicList(albumPicList);
        // 相册点赞数量
        long likeCount = likeService.findTargetLikeCount(AlumniEnum.album.getAlumniType(), album.getId());
        // 当前登录用户的点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findTargetLikeStatus(AlumniEnum.album.getAlumniType(), album.getId(), hostHolder.getUser().getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);
        //活动收藏数量
        long collectCount = collectService.findTargetCollectCount(AlumniEnum.album.getAlumniType(), album.getId());
        //当前用户的收藏状态
        int collectStatus = hostHolder.getUser() == null ? 0 :
                collectService.findTargetCollectStatus(AlumniEnum.album.getAlumniType(), album.getId(), hostHolder.getUser().getId());
        model.addAttribute("collectCount", collectCount);
        model.addAttribute("collectStatus", collectStatus);
        //相册
        model.addAttribute("album", album);

        //遍历评论集合获取每个评论的用户名、头像、内容、时间
        List<Comment> commentList = commentService.findCommentByType(AlumniEnum.album.getCode(), id);
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        int msgNum = 0;
        if (commentList != null){
            msgNum += commentList.size();
            for (Comment comment : commentList) {
                //存储对相册的评论
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

        //评论
        model.addAttribute("comments", commentVoList);
        //总留言数
        model.addAttribute("msgNum", msgNum);
        return "album/details";
    }
}

