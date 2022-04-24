package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.*;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.*;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户表(User)表控制层
 *
 * @Author: Administrator
 * @since 2022-02-28 09:28:22
 */
@Controller("user_user")
@RequestMapping("/user")
public class UserController {
    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Resource
    private NewsService newsService;

    @Resource
    private ActivityService activityService;

    @Resource
    private AlbumService albumService;

    @Resource
    private PostService postService;

    @Resource
    private CollectService collectService;

    @Resource
    private UserEventLogService userEventLogService;

    /**
     * 页头判断用户是否登录
     * @return 0-代表登录 1-代表未登录
     */
    @GetMapping("/isLogin")
    @ResponseBody
    public String isLogin(){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        if (user != null){
            map.put("headPic", user.getHeadPic());
        }
        return user != null ? AlumniUtil.getJSONString(0,"已登录", map) : AlumniUtil.getJSONString(1,"未登录", null);
    }


    /**
     * 进入到我的资料页面
     * @return String
     */
    @GetMapping("/my/oneself")
    public String getProfile(Model model){
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
            return "/user/system/operate-result";
        }
        //用户本身
        model.addAttribute("user", user);
        //用户的动态、发布的内容、收藏的内容
        List<UserEventLog> userEventLogList = userEventLogService.findByUserId(user.getId());
        model.addAttribute("userEventLogList", userEventLogList);

        //用户发布的活动、用户参与的活动
        Map<String, Object> activityMap = new HashMap<>();

        List<Activity> publishList = activityService.findByUserId(user.getId());
        activityMap.put("publishList", publishList);
        List<Activity> enrollList = activityService.findEnrollByUserId(user.getId());
        activityMap.put("enrollList", enrollList);

        model.addAttribute("activityMap", activityMap);

        List<Album> albumList = albumService.findByUserId(user.getId());
        model.addAttribute("albumList", albumList);

        List<Post> postList = postService.findByUserId(user.getId());
        model.addAttribute("postList", postList);

        //找出收藏的新闻、活动、相册、帖子
        Map<String, Object> collectMap = new HashMap<>();

        List<Integer> newsIds = collectService.findCollector(AlumniEnum.news.getAlumniType(), user.getId());
        collectMap.put("newsList", newsService.findCollectNews(newsIds));

        List<Integer> activityIds = collectService.findCollector(AlumniEnum.activity.getAlumniType(), user.getId());
        collectMap.put("activityList", activityService.findCollectActivity(activityIds));

        List<Integer> albumIds = collectService.findCollector(AlumniEnum.album.getAlumniType(), user.getId());
        collectMap.put("albumList", albumService.findCollectAlbum(albumIds));

        List<Integer> postIds = collectService.findCollector(AlumniEnum.post.getAlumniType(), user.getId());
        collectMap.put("postList", postService.findCollectPost(postIds));

        model.addAttribute("collectMap", collectMap);
        return "/user/profile/oneself";
    }

    /**
     * 进入到其它人的资料页面
     * @return String
     */
    @GetMapping("/other/{id}")
    public String getProfile(Model model, @PathVariable("id") Integer id){
        User user = userService.selectById(id);
        if (user == null){
            model.addAttribute("msg", "用户不存在!");
            model.addAttribute("target", "/user/index");
        }
        model.addAttribute("user", user);
        return "/user/profile/other";
    }

    /**
     * 进入到修改资料(主要)页面
     * @return String
     */
    @GetMapping("/setting/profile")
    public String getSettingProfile(Model model){
        User user = hostHolder.getUser();
        model.addAttribute("user", user);
        return "/user/profile/setting-profile";
    }

    /**
     * 进入到修改资料(修改密码)页面
     * @return String
     */
    @GetMapping("/setting/password")
    public String getSettingPassword(){
        return "/user/profile/setting-password";
    }

    /**
     * 修改个人资料
     * @param model
     * @param user 用户信息
     * @return
     */
    @PostMapping("/update/profile")
    public String updateProfile(Model model, User user){
        User hostUser = hostHolder.getUser();
        if (hostUser == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else{
            //设置用户ID
            user.setId(hostUser.getId());
            userService.updateProfile(user);
            model.addAttribute("msg", "个人资料修改成功!");
            model.addAttribute("target", "/user/my/oneself");
        }
        return "/user/system/operate-result";
    }

    @PostMapping("/update/password")
    public String updatePassword(Model model,
                                 @RequestParam("oldPwd") String oldPwd,
                                 @RequestParam("pwd") String pwd){
        Map<String, Object> map = userService.updatePassword(oldPwd, pwd);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg", "密码修改成功!");
            model.addAttribute("target", "/user/login");
        }else{
            model.addAttribute("msg", map.get("msg"));
            model.addAttribute("target", "/user/my/setting/password");
        }
        return "/user/system/operate-result";
        }

}
