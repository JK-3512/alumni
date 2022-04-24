package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.CollectService;
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
 * @Date: 2022/2/17 9:56
 * Description 收藏控制层
 */
@Controller
@RequestMapping("/user/")
public class CollectController {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private CollectService collectService;

    /**
     * 收藏
     * @param targetType 业务类型
     * @param targetId 业务ID
     * @return
     */
    @PostMapping("/collect")
    @ResponseBody
    public String collect(int targetType, int targetId) {
        User user = hostHolder.getUser();
        if (user == null){
            return AlumniUtil.getJSONString(1,"请先登录!",null);
        }
        // 收藏
        collectService.collect(AlumniEnum.fromCode(targetType).getAlumniType(), targetId, user.getId());
        // 收藏数量
        long collectCount = collectService.findTargetCollectCount(AlumniEnum.fromCode(targetType).getAlumniType(), targetId);
        // 当前用户收藏状态
        int collectStatus = collectService.findTargetCollectStatus(AlumniEnum.fromCode(targetType).getAlumniType(), targetId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("collectCount", collectCount);
        map.put("collectStatus", collectStatus);

        return AlumniUtil.getJSONString(0, null, map);
    }



}
