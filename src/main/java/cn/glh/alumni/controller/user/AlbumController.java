package cn.glh.alumni.controller.user;

import com.github.pagehelper.PageInfo;
import cn.glh.alumni.base.*;
import cn.glh.alumni.entity.Album;
import cn.glh.alumni.service.impl.AlbumServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * 相册表(Album)表控制层
 *
 * @author makejava
 * @since 2022-02-20 17:21:00
 */
@Controller
@RequestMapping("/user/album")
public class AlbumController {
    /**
     * 服务对象
     */
    @Resource
    private AlbumServiceImpl albumServiceImpl;

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return Result对象
     */
    @PostMapping(value = "/queryPage")
    public Result<PageInfo<Album>> queryPage(@RequestBody BasePage basePage) {
        PageInfo<Album> page = albumServiceImpl.queryByPage(basePage);
        return Result.ok(page);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping(value = "/get/{id}")
    public Result<Album> queryById(@PathVariable("id") Integer id) {
    Album result = albumServiceImpl.queryById(id);
        if(Objects.nonNull(result)){
            return Result.ok(result);
        }
        return Result.fail(0,"查询失败");
    }

    /**
     * 新增数据
     *
     * @param album 实体
     * @return 新增结果
     */
    @PostMapping(value = "/insert")
    public Result insert(@RequestBody Album album) {
        int result = albumServiceImpl.insert(album);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"新增失败");
    }

    /**
     * 编辑数据
     *
     * @param album 实体
     * @return 编辑结果
     */
    @PutMapping(value = "/update")
    public Result update(@RequestBody Album album) {
        int result = albumServiceImpl.update(album);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"修改失败");
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        int result = albumServiceImpl.deleteById(id);
        if (result > 0) {
          return Result.ok();
        }
        return Result.fail(0,"删除失败");
    }

    /**
     * 进入到发布相册页面
     * @param model
     * @return
     */
    @GetMapping("/publish")
    public String getPublish(Model model){
        return "album/publish";
    }

    @PostMapping("/add")
    public String addAlbum(Model model, Album album){
        System.out.print(album.toString());
        return "album/publish";
    }
}

