package cn.glh.alumni.controller.admin;

import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.Post;
import cn.glh.alumni.service.PostService;
import cn.glh.alumni.util.AlumniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller("post_admin")
@RequestMapping("/admin/post")
public class PostController {

    @Resource
    private PostService postService;

    @GetMapping("/list")
    public String getListPage(){
        return "/admin/post/list";
    }

    @GetMapping("/add")
    public String getAddPage(){
        return "/admin/post/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public String AddPost(@RequestBody Post post){
        int i = postService.insertPost(post);
        return i > 0 ? AlumniUtil.getJSONString(0, "新增成功") : AlumniUtil.getJSONString(1, "新增失败");
    }

    @GetMapping("/all")
    @ResponseBody
    public String AllPost(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Post> postList = postService.queryByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",postList.size(), postList);
    }

    @GetMapping("/edit/{id}")
    public String editPost(Model model, @PathVariable("id") Integer id){
        Post post = postService.selectById(id);
        model.addAttribute("post", post);
        return "/admin/post/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public String editPost(@RequestBody Post post){
        int i = postService.updatePost(post);
        return i > 0 ? AlumniUtil.getJSONString(0, "修改成功") : AlumniUtil.getJSONString(1, "修改失败");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public String removePost(@PathVariable("id") Integer id){
        int i = postService.deleteById(id);
        return i > 0 ? AlumniUtil.getJSONString(0, "删除成功") : AlumniUtil.getJSONString(1, "删除失败");
    }

    @GetMapping("/audit")
    public String getAuditPage(){
        return "/admin/post/audit";
    }

    @GetMapping("/audit/list")
    @ResponseBody
    public String auditList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Post> postList = postService.queryAuditByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",postList.size(), postList);
    }

    @GetMapping("/audit/details/{id}")
    public String getDetailPage(Model model, @PathVariable("id") Integer id){
        Post post = postService.selectById(id);
        model.addAttribute("post", post);
        return "/admin/post/details";
    }

    @PostMapping("/audit/pass/{id}")
    @ResponseBody
    public String passPost(@PathVariable("id") Integer id){
        int i = postService.updateState(id, 1);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/noPass/{id}")
    @ResponseBody
    public String noPassPost(@PathVariable("id") Integer id){
        int i = postService.updateState(id, 2);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/batchPass/{ids}")
    @ResponseBody
    public String batchPassPost(@PathVariable("ids") List<Integer> ids){
        try {
            postService.updateStateList(ids, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }

    @PostMapping("/audit/batchNoPass/{ids}")
    @ResponseBody
    public String batchNoPassPost(@PathVariable("ids") List<Integer> ids){
        try {
            postService.updateStateList(ids, 2);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }

    @GetMapping("/search")
    @ResponseBody
    public String editPost(@RequestParam("title") String title, @RequestParam("sort") Integer sort,
                               @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Post> postList = postService.searchPost(title, sort);
        return AlumniUtil.getAdminJSONString(0,"成功",postList.size(), postList);
    }
}
