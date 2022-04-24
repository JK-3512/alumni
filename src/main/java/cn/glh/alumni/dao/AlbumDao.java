package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Activity;
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
    List<Album> findAllAlbum(@Param("offset") Integer offset, @Param("rows") Integer rows);

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
    List<Album> findAlbumBySort(@Param("sort") String sort, @Param("offset") Integer offset, @Param("rows") Integer rows);

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

    /**
     * 分页查询已审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Album> queryByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 分页查询未审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<Album> queryAuditByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    int updateState(@Param("id") Integer id, @Param("state") Integer state);

    /**
     * 找到最新的相册
     * @return 相册集合
     */
    Album getNewAlbum();

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    Integer getCountBySort(@Param("sort") String sort);

    /**
     * 获取总条码
     * @return
     */
    Integer getCount();

    /**
     * 相册检索
     * @param search 关键词
     * @return
     */
    List<Album> searchAlbum(@Param("search") String search);
}

