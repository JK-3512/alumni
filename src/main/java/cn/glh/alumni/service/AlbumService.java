package cn.glh.alumni.service;

import cn.glh.alumni.entity.Album;
import cn.glh.alumni.base.BasePage;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 相册表(Album)表服务接口
 *
 * @author makejava
 * @since 2022-02-20 17:21:00
 */
public interface AlbumService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Album selectById(Integer id);

    /**
     * 分页查询
     *
     * @param basePage 分页参数
     * @return 查询结果
     */
    PageInfo<Album> queryByPage(BasePage basePage);

    /**
     * 新增数据
     *
     * @param album 实例对象
     * @return 实例对象
     */
    int insert(Album album);

    /**
     * 修改数据
     *
     * @param album 实例对象
     * @return 实例对象
     */
    int update(Album album);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Integer id);

    /**
     * 发布相册
     * @param album 相册对象
     */
    void insertAlbum(Album album);

    /**
     * 相册列表
     * @param sort 分类
     * @return 相册对象集合
     */
    List<Album> getAlbumList(Integer sort);

    /**
     * 获取相册下属所有的图片
     * @param id 相册ID
     * @return 图片集合
     */
    List<String> getAlbumPicList(Integer id);
}
