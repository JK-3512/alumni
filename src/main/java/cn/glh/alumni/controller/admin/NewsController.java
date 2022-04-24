package cn.glh.alumni.controller.admin;

import cn.glh.alumni.entity.News;
import cn.glh.alumni.service.NewsService;
import cn.glh.alumni.util.AlumniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller("news_admin")
@RequestMapping("/admin/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @GetMapping("/list")
    public String getListPage(){
        return "/admin/news/list";
    }

    @GetMapping("/add")
    public String getAddPage(){
        return "/admin/news/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public String AddNews(@RequestBody News news){
        int i = newsService.insertNews(news);
        return i > 0 ? AlumniUtil.getJSONString(0, "新增成功") : AlumniUtil.getJSONString(1, "新增失败");
    }

    @GetMapping("/all")
    @ResponseBody
    public String AllNews(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<News> newsList = newsService.queryByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",newsList.size(), newsList);
    }

    @GetMapping("/edit/{id}")
    public String editNews(Model model, @PathVariable("id") Integer id){
        News news = newsService.selectById(id);
        model.addAttribute("news", news);
        return "/admin/news/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public String editNews(@RequestBody News news){
        int i = newsService.updateNews(news);
        return i > 0 ? AlumniUtil.getJSONString(0, "修改成功") : AlumniUtil.getJSONString(1, "修改失败");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public String removeNews(@PathVariable("id") Integer id){
        int i = newsService.deleteById(id);
        return i > 0 ? AlumniUtil.getJSONString(0, "删除成功") : AlumniUtil.getJSONString(1, "删除失败");
    }

    @GetMapping("/audit")
    public String getAuditPage(){
        return "/admin/news/audit";
    }

    @GetMapping("/audit/list")
    @ResponseBody
    public String auditList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<News> newsList = newsService.queryAuditByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",newsList.size(), newsList);
    }

    @GetMapping("/audit/details/{id}")
    public String getDetailPage(Model model, @PathVariable("id") Integer id){
        News news = newsService.selectById(id);
        model.addAttribute("news", news);
        return "/admin/news/details";
    }

    @PostMapping("/audit/pass/{id}")
    @ResponseBody
    public String passNews(@PathVariable("id") Integer id){
        int i = newsService.updateState(id, 1);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/noPass/{id}")
    @ResponseBody
    public String noPassNews(@PathVariable("id") Integer id){
        int i = newsService.updateState(id, 2);
        return i > 0 ? AlumniUtil.getJSONString(0, "审核成功") : AlumniUtil.getJSONString(1, "审核失败");
    }

    @PostMapping("/audit/batchPass/{ids}")
    @ResponseBody
    public String batchPassNews(@PathVariable("ids") List<Integer> ids){
        try {
            newsService.updateStateList(ids, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }

    @PostMapping("/audit/batchNoPass/{ids}")
    @ResponseBody
    public String batchNoPassNews(@PathVariable("ids") List<Integer> ids){
        try {
            newsService.updateStateList(ids, 2);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1, "审核失败");
        }
        return AlumniUtil.getJSONString(0, "审核成功");
    }
}
