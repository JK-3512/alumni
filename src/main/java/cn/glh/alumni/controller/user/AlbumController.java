package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.*;
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
@Controller("album_user")
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
    private LoginService loginService;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    /**
     * 进入到相册列表页面
     * @return String
     */
    @GetMapping("/sort/{sort}")
    public String getAlbumList(
            Model model, @PathVariable(value = "sort") Integer sort,
            @RequestParam(value = "page",required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "limit",required = false, defaultValue = "3") Integer limit,
            @RequestParam(value = "isPage",required = false, defaultValue = "false") Boolean isPage
            ){
        Album newAlbum = albumService.getNewAlbum();
        List<Album> albumList = albumService.getAlbumList(sort, page, limit);
        model.addAttribute("newAlbum", newAlbum);
        model.addAttribute("albumList", albumList);
        //分页需要用到
        model.addAttribute("sort",sort);
        //获取该类别下的总条码
        model.addAttribute("count", albumService.getCountBySort(sort));
        //除第一次访问外，其余返回th:fragment内容，只更新内容列表
        if (isPage){
            return "/user/album/list::content";
        }
        return "/user/album/list";
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
            return "/user/system/operate-result";
        }
        return "/user/album/publish";
    }

    /**
     * 进入到更新相册页面
     * @param model
     * @return
     */
    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable("id") Integer id){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
            return "/user/system/operate-result";
        }
        Album album = albumService.selectById(id);
        model.addAttribute("album", album);
        return "/user/album/update";
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
            model.addAttribute("target", "/user/album/category/0");
        }
        return "/user/system/operate-result";
    }

    /**
     * 更新相册
     * @param model
     * @param album
     * @return
     */
    @PostMapping("/update")
    public String updateAlbum(Model model, Album album){
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            albumService.updateAlbum(album);
            model.addAttribute("msg", "相册更新成功!");
            model.addAttribute("target", "/user/album/details/" + album.getId());
        }
        return "/user/system/operate-result";
    }

    /**
     * 进入到相册详情页
     * @return String
     */
    @GetMapping("/details/{id}")
    public String getDetails(Model model, @PathVariable("id") int id) {
        Album album = albumService.selectById(id);
        //设置作者
        album.setAuthor(loginService.selectById(album.getUserId()).getNickName());
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
                commentVo.put("user", loginService.selectById(comment.getFromUserId()));
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
                        replyVo.put("user", loginService.selectById(reply.getFromUserId()));
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
        return "/user/album/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAlbum(Model model, @PathVariable("id") Integer id){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            albumService.deleteById(id);
            model.addAttribute("msg", "相册删除成功!");
            model.addAttribute("target", "/user/my/oneself");
        }
        return "/user/system/operate-result";
    }

    @GetMapping("/search")
    public String searchAlbum(Model model, @RequestParam("search") String search){
        Album newAlbum = albumService.getNewAlbum();
        model.addAttribute("newAlbum", newAlbum);
        List<Album> albumList = albumService.searchAlbum(search, null);
        model.addAttribute("albumList", albumList);
        model.addAttribute("search", search);
        model.addAttribute("count", albumList.size());
        return "/user/album/search";
    }
}

