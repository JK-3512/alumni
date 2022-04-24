package cn.glh.alumni.controller.admin;

import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.service.ActivityService;
import cn.glh.alumni.util.AlumniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller("activity_admin")
@RequestMapping("/admin/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    @GetMapping("/list")
    public String getListPage(){
        return "/admin/activity/list";
    }

    @GetMapping("/add")
    public String getAddPage(){
        return "/admin/activity/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public String AddActivity(@RequestBody Activity activity){
        try {
            activityService.insertActivity(activity);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "新增失败");
        }
        return AlumniUtil.getJSONString(0, "新增成功");
    }

    @GetMapping("/all")
    @ResponseBody
    public String AllActivity(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Activity> activityList = activityService.queryByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",activityList.size(), activityList);
    }

    @GetMapping("/edit/{id}")
    public String editActivity(Model model, @PathVariable("id") Integer id){
        Activity activity = activityService.selectById(id);
        model.addAttribute("activity", activity);
        return "/admin/activity/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public String editActivity(@RequestBody Activity activity){
        int i = activityService.updateActivity(activity);
        return i > 0 ? AlumniUtil.getJSONString(0, "修改成功") : AlumniUtil.getJSONString(1, "修改失败");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public String removeActivity(@PathVariable("id") Integer id){
        int i = activityService.deleteById(id);
        return i > 0 ? AlumniUtil.getJSONString(0, "删除成功") : AlumniUtil.getJSONString(1, "删除失败");
    }

    @GetMapping("/audit")
    public String getAuditPage(){
        return "/admin/activity/audit";
    }

    @GetMapping("/audit/list")
    @ResponseBody
    public String auditList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<Activity> activityList = activityService.queryAuditByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",activityList.size(), activityList);
    }

    @GetMapping("/audit/details/{id}")
    public String getDetailPage(Model model, @PathVariable("id") Integer id){
        Activity activity = activityService.selectById(id);
        model.addAttribute("activity", activity);
        return "/admin/activity/details";
    }

    @PostMapping("/audit/pass/{id}")
    @ResponseBody
    public String passActivity(@PathVariable("id") Integer id){
        int i = activityService.updateState(id, 1);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/noPass/{id}")
    @ResponseBody
    public String noPassActivity(@PathVariable("id") Integer id){
        int i = activityService.updateState(id, 2);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/batchPass/{ids}")
    @ResponseBody
    public String batchPassActivity(@PathVariable("ids") List<Integer> ids){
        try {
            activityService.updateStateList(ids, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }

    @PostMapping("/audit/batchNoPass/{ids}")
    @ResponseBody
    public String batchNoPassActivity(@PathVariable("ids") List<Integer> ids){
        try {
            activityService.updateStateList(ids, 2);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }
}
