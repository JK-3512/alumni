package cn.glh.alumni.service.impl;

import cn.glh.alumni.entity.Album;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.AlbumDao;
import cn.glh.alumni.service.AlbumService;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import java.util.List;

/**
 * 相册表(Album)表服务实现类
 *
 * @author makejava
 * @since 2022-02-20 17:21:00
 */
@Service("albumServiceImpl")
public class AlbumServiceImpl implements AlbumService {
    @Resource
    private AlbumDao albumDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Album queryById(Integer id) {
        return this.albumDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    @Override
    public PageInfo<Album> queryByPage(BasePage basePage) {
        return PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize(), "id desc")
                .doSelectPageInfo( () -> albumDao.queryAll());
    }

    /**
     * 新增数据
     *
     * @param album 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(Album album) {
        return this.albumDao.insert(album);
    }

    /**
     * 修改数据
     *
     * @param album 实例对象
     * @return 实例对象
     */
    @Override
    public int update(Album album) {
        return this.albumDao.update(album);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(Integer id) {
        return this.albumDao.deleteById(id);
    }
}
