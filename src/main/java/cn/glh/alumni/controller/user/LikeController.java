package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.LikeService;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Administrator
 * @Date: 2022/2/15 12:02
 * Description 点赞控制层
 */
@Controller
@RequestMapping("/user/")
public class LikeController {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    /**
     * 点赞
     * @param targetType 业务类型
     * @param targetId 业务ID
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int targetType, int targetId) {
        User user = hostHolder.getUser();
        if (user == null){
            return AlumniUtil.getJSONString(1,"请先登录!",null);
        }
        // 点赞
        likeService.like(AlumniEnum.fromCode(targetType).getAlumniType(), targetId, user.getId());
        // 点赞数量
        long likeCount = likeService.findTargetLikeCount(AlumniEnum.fromCode(targetType).getAlumniType(), targetId);
        // 当前用户点赞状态
        int likeStatus = likeService.findTargetLikeStatus(AlumniEnum.fromCode(targetType).getAlumniType(), targetId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return AlumniUtil.getJSONString(0, null, map);
    }

}
