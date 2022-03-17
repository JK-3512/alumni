package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 相册表(Album)表数据库访问层
 *
 * @author makejava
 * @since 2022-02-20 16:03:13
 */
 @Mapper
public interface AlbumDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Album selectById(Integer id);
    
    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<Album> queryAll();

    /**
     * 新增数据
     *
     * @param album 实例对象
     * @return 影响行数
     */
    int insertAlbum(Album album);

    /**
     * 修改数据
     *
     * @param album 实例对象
     * @return 影响行数
     */
    int update(Album album);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 插入多图片
     * @param id 相册ID
     * @param albumPicList 图片集合
     */
    void insertAlbumPic(@Param("id") Integer id, @Param("albumPicList") List<String> albumPicList);

    /**
     * 获取相册下属所有的图片
     * @param id 相册ID
     * @return 图片集合
     */
    List<String> getAlbumPicList(Integer id);

    /**
     * 相册列表
     * @param sort 类别
     * @return 相册集合
     */
    List<Album> getAlbumList(String sort);

    /**
     * 找出用户发布的相册
     * @param userId 用户ID
     * @return 相册集合
     */
    List<Album> findByUserId(Integer userId);

    /**
     * 删除相册对应的所有图片
     * @param id 相册ID
     */
    void deleteAlbumPic(Integer id);
}

