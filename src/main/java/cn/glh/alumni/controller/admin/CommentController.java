package cn.glh.alumni.controller.admin;

import cn.glh.alumni.entity.Comment;
import cn.glh.alumni.service.CommentService;
import cn.glh.alumni.util.AlumniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller("comment_admin")
@RequestMapping("/admin/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("/list")
    public String getCommentListPage() {
        return "/admin/comment/list";
    }

    @GetMapping("/all")
    @ResponseBody
    public String AllComment(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Comment> commentList = commentService.queryByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",commentList.size(), commentList);
    }

    @GetMapping("/add")
    public String addComment(){ return "/admin/comment/add"; }

    @PostMapping("/add")
    @ResponseBody
    public String addComment(@RequestBody Comment comment) {
        try {
            commentService.insertComment(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1,"新增失败");
        }
        return AlumniUtil.getJSONString(0,"新增成功");
    }

    @GetMapping("/edit/{id}")
    public String getCommentEditPage(Model model, @PathVariable("id") Integer id) {
        Comment comment = commentService.selectById(id);
        model.addAttribute("comment",comment);
        return "/admin/comment/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public String editComment(@RequestBody Comment comment) {
        try {
            commentService.updateComment(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1,"修改失败");
        }
        return AlumniUtil.getJSONString(0,"修改成功");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public String deleteComment(@PathVariable("id") Integer id) {
        int i = commentService.deleteById(id);
        //删除用户，需要先删除用户发布的内容和参与的活动及评论/回复
        return i > 0 ? AlumniUtil.getJSONString(0,"删除成功") : AlumniUtil.getJSONString(1,"删除失败");
    }

    @DeleteMapping("/batchRemove/{ids}")
    @ResponseBody
    public String deleteComment(@PathVariable("ids") List<Integer> ids) {
        System.out.println(ids);
        //删除用户，需要先删除用户发布的内容和参与的活动及评论/回复
        return AlumniUtil.getJSONString(0,"删除成功");
    }
}
