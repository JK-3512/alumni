package cn.glh.alumni.controller.admin;

import cn.glh.alumni.entity.User;
import cn.glh.alumni.service.UserService;
import cn.glh.alumni.util.AlumniUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller("user_admin")
@RequestMapping("/admin/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public String getUserListPage() {
        return "/admin/user/list";
    }

    @GetMapping("/all")
    @ResponseBody
    public String AllUser(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        List<User> userList = userService.queryByPage(page, limit);
        return AlumniUtil.getAdminJSONString(0,"成功",userList.size(), userList);
    }

    @GetMapping("/add")
    public String addUser(){ return "/admin/user/add"; }

    @PostMapping("/add")
    @ResponseBody
    public String addUser(@RequestBody User user) {
        try {
            userService.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1,"新增失败");
        }
        return AlumniUtil.getJSONString(0,"新增成功");
    }

    @GetMapping("/edit/{id}")
    public String getUserEditPage(Model model, @PathVariable("id") Integer id) {
        User user = userService.selectById(id);
        model.addAttribute("user",user);
        return "/admin/user/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public String editUser(@RequestBody User user) {
        try {
            userService.updateProfile(user);
        } catch (Exception e) {
            e.printStackTrace();
            return AlumniUtil.getJSONString(1,"修改失败");
        }
        return AlumniUtil.getJSONString(0,"修改成功");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable("id") Integer id) {
        System.out.println(id);
        //删除用户，需要先删除用户发布的内容和参与的活动及评论/回复
        return AlumniUtil.getJSONString(0,"删除成功");
    }

    @DeleteMapping("/batchRemove/{ids}")
    @ResponseBody
    public String deleteUser(@PathVariable("ids") List<Integer> ids) {
        System.out.println(ids);
        //删除用户，需要先删除用户发布的内容和参与的活动及评论/回复
        return AlumniUtil.getJSONString(0,"删除成功");
    }


}
