package cn.glh.alumni.controller.user;

import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.service.CommentService;
import cn.glh.alumni.util.HostHolder;
import cn.glh.alumni.entity.Comment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 评论回复表(Comment)表控制层
 *
 * @author Administrator
 * @since 2022-02-14 09:45:39
 */
@Controller("comment_user")
@RequestMapping("/user/comment")
public class CommentController {
    /**
     * 服务对象
     */
    @Resource
    private CommentService commentService;

    @Resource
    private HostHolder hostHolder;

    /**
     * 新增评论/回复
     *
     * @param comment 实体
     * @return 新增结果
     */
    @PostMapping(value = "/add")
    public String insertComment(Model model,  Comment comment) {
        //判断用户是否登录
        User user = hostHolder.getUser();
        if (user == null){
            model.addAttribute("msg", "请先登录!");
            model.addAttribute("target", "/user/login");
        }else {
            commentService.insertComment(comment);
            //评论/回复成功都要跳到相同的界面
            String type;
            Integer id;
            //回复成功，中转到操作结果界面，最后跳转到模块详情界面
            if (comment.getTargetType() == AlumniEnum.comment.getCode()){
                Comment cmt = commentService.findCommentById(comment.getTargetId());
                type = AlumniEnum.fromCode(cmt.getTargetType()).getAlumniType();
                id = cmt.getTargetId();
            }else{
                //评论成功，中转到操作结果界面，最后跳转到模块详情界面
                        type = AlumniEnum.fromCode(comment.getTargetType()).getAlumniType();
                id = comment.getTargetId();

            }
            model.addAttribute("target", "/user/"+ type + "/details/" + id);
            model.addAttribute("msg", "发表成功!");

        }
        return "/user/system/operate-result";
    }


}

