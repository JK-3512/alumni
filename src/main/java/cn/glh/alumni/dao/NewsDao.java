package cn.glh.alumni.dao;

import cn.glh.alumni.entity.News;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻资讯表(News)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-29 14:32:22
 */
 @Mapper
public interface NewsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    News selectById(Integer id);
    
    /**
     * 查询全部
     *
     * @return 对象列表
     */
    List<News> selectAll();

    /**
     * 新增数据
     *
     * @param news 实例对象
     * @return 影响行数
     */
    int insertNews(News news);

    /**
     * 修改数据
     *
     * @param news 实例对象
     * @return 影响行数
     */
    int updateNews(News news);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 分页查询已审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<News> queryByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 分页查询未审核信息
     * @param offset 返回记录行的偏移量
     * @param rows 返回记录行的最大数目
     * @return 对象列表
     */
    List<News> queryAuditByPage(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    int updateState(@Param("id") Integer id, @Param("state") Integer state);

    /**
     * 找出所有资讯
     * @return 资讯集合
     */
    List<News> findAllNews(@Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 找出该分类下的所有资讯
     * @param sort 资讯类型
     * @return
     */
    List<News> findSortNews(@Param("sort") String sort, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 资讯板块的封面（最新发布）
     * @return 新闻集合
     */
    List<News> getCoverList();

    /**
     * 获取总条码
     * @return
     */
    Integer getCount();

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    Integer getCountBySort(@Param("sort") String sort);

    /**
     * 资讯检索
     * @param search 关键词
     * @return
     */
    List<News> searchNews(@Param("search") String search);
}

