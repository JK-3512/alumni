package cn.glh.alumni.dao;

import cn.glh.alumni.entity.Album;
import org.apache.ibatis.annotations.Mapper;
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
    Album queryById(Integer id);
    
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
    int insert(Album album);

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

}

