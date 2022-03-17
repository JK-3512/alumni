package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.*;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.*;
import cn.glh.alumni.util.HostHolder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 活动表(Activity)表控制层
 *
 * @author Administrator
 * @since 2022-02-08 10:20:43
 */
@Controller
@RequestMapping("/user/activity")
public class ActivityController {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private LoginService loginService;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    @Resource
    private ActivityService activityService;

    /**
     * 进入到活动列表页面
     * @return String
     */
    @GetMapping("/category/{sort}")
    public String getActivityList(Model model, @PathVariable(value = "sort") Integer sort){
        List<Activity> activityList = activityService.getActivityList(sort);
        model.addAttribute("activityList", activityList);
        return "activity/list";
    }

    /**
     * 进入到活动发布页面
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
        return "activity/publish";
    }

    /**
     * 进入到活动更新页面
     * @return String
     */
    @GetMapping("/update/{id}")
    public String getUpdatePage(Model model, @PathVariable("id") Integer id){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
            return "operate-result";
        }
        Activity activity = activityService.selectById(id);
        model.addAttribute("activity", activity);
        return "activity/update";
    }

    /**
     * 发布活动
     * @param activity
     * @return
     */
    @PostMapping("/add")
    public String addActivity(Model model, Activity activity){
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            activityService.insertActivity(activity);
            model.addAttribute("msg", "活动发布成功!");
            model.addAttribute("target", "/user/activity/list");
        }
        return "operate-result";
    }

    /**
     * 活动更新
     * @return String
     */
    @PostMapping("/update")
    public String updateActivity(Model model, Activity activity){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            activityService.updateActivity(activity);
            model.addAttribute("msg", "活动更新成功!");
            model.addAttribute("target", "/user/activity/details/" + activity.getId());
        }
        return "operate-result";
    }

    /**
     * 活动报名
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/enroll/{id}")
    public String enrollActivity(Model model, @PathVariable("id") Integer id){
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            Map<String, Object> map = activityService.insertEnrollUser(id, user.getId());
            if (map == null || map.isEmpty()){
                model.addAttribute("msg", "活动报名成功，不要忘记参加呦!");
            }else{
                //重复报名或人数已满
                model.addAttribute("msg", map.get("msg"));
            }
            model.addAttribute("target", "/user/activity/details/" + id);
        }
        return "operate-result";
    }


    /**
     * 进入到活动详情页
     * @return String
     */
    @GetMapping("/details/{id}")
    public String getDetails(@PathVariable("id") int id,Model model) {
        Activity activity = activityService.selectById(id);
        //设置作者
        activity.setAuthor(userService.selectById(activity.getUserId()).getNickName());
        List<ActivityEnroll> enrollList = activityService.getEnrollUser(id);
        // 活动点赞数量
        long likeCount = likeService.findTargetLikeCount(AlumniEnum.activity.getAlumniType(), activity.getId());
        // 当前登录用户的点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findTargetLikeStatus(AlumniEnum.activity.getAlumniType(), activity.getId(), hostHolder.getUser().getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);
        //活动收藏数量
        long collectCount = collectService.findTargetCollectCount(AlumniEnum.activity.getAlumniType(), activity.getId());
        //当前用户的收藏状态
        int collectStatus = hostHolder.getUser() == null ? 0 :
                collectService.findTargetCollectStatus(AlumniEnum.activity.getAlumniType(), activity.getId(), hostHolder.getUser().getId());
        model.addAttribute("collectCount", collectCount);
        model.addAttribute("collectStatus", collectStatus);
        //活动、报名人
        model.addAttribute("activity", activity);
        model.addAttribute("enrollNum", enrollList.size());
        model.addAttribute("enrollList", enrollList);

        //遍历评论集合获取每个评论的用户名、头像、内容、时间
        List<Comment> commentList = commentService.findCommentByType(AlumniEnum.activity.getCode(), id);
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        int msgNum = 0;
        if (commentList != null){
            msgNum += commentList.size();
            for (Comment comment : commentList) {
                //存储对活动的评论
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
        return "activity/details";
    }


    /**
     * 活动删除
     * @param model
     * @param id 活动ID
     * @return
     */
    @GetMapping("/delete/{id}")
    public String deleteActivity(Model model, @PathVariable("id") Integer id){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            activityService.deleteById(id);
            model.addAttribute("msg", "活动删除成功!");
            model.addAttribute("target", "/user/my/oneself");
        }
        return "operate-result";
    }
}



