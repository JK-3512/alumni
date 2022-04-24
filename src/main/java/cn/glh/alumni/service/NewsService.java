package cn.glh.alumni.service;

import cn.glh.alumni.dao.NewsDao;
import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.News;
import cn.glh.alumni.entity.enums.ActivityEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.entity.enums.NewsEnum;
import cn.glh.alumni.util.HostHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新闻资讯表(News)表服务接口
 *
 * @author makejava
 * @since 2022-03-29 14:32:22
 */
@Service
public class NewsService {

    @Resource
    private NewsDao newsDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    public News selectById(Integer id){ return newsDao.selectById(id);}

    /**
     * 新增数据
     *
     * @param news 实例对象
     * @return 实例对象
     */
    public int insertNews(News news){
        //设置分类、发布时间，发布人id
        news.setSort(NewsEnum.fromCode(Integer.valueOf(news.getSort())).getNewsType());
        news.setAdminId(hostHolder.getUser().getId());
        news.setCreateTime(new Date());
        news.setState(0);
        return newsDao.insertNews(news);
    }

    /**
     * 修改数据
     *
     * @param news 实例对象
     * @return 实例对象
     */
    public int updateNews(News news){
        //重设分类
        if (news.getSort() != null){
            news.setSort(NewsEnum.fromCode(Integer.valueOf(news.getSort())).getNewsType());
        }
        return newsDao.updateNews(news);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer id){
        //清除reids缓存该资讯的点赞数据
        likeService.deleteTargetLike(AlumniEnum.news.getAlumniType(), id);
        //清除reids缓存中该资讯的收藏数据
        collectService.deleteTargetCollect(AlumniEnum.news.getAlumniType(), id);
        //清除该新闻的评论及回复
        commentService.deleteComment(AlumniEnum.news.getCode(), id);
        return newsDao.deleteById(id);
    }

    /**
     * 分页查询已审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return 新闻集合
     */
    public List<News> queryByPage(Integer page, Integer limit) {
        return newsDao.queryByPage((page - 1) * limit ,limit);
    }

    /**
     * 分页查询审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return 新闻集合
     */
    public List<News> queryAuditByPage(Integer page, Integer limit) {
        return newsDao.queryAuditByPage((page - 1) * limit ,limit);
    }

    /**
     * 修改资讯审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    public int updateState(Integer id, Integer state){
        return newsDao.updateState(id, state);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStateList(List<Integer> ids, Integer state){
        for (Integer id : ids) {
            this.updateState(id, state);
        }
    }

    /**
     * 进入到资讯列表页面
     * @param sort 分类
     * @return 资讯集合
     */
    public List<News> getNewsList(Integer sort, Integer page, Integer limit) {
        List<News> newsList = null;
        //全部查询
        if (sort == NewsEnum.all.getCode()){
            newsList  = newsDao.findAllNews((page - 1) * limit, limit);
        }else{
            newsList = newsDao.findSortNews(NewsEnum.fromCode(sort).getNewsType(), (page - 1) * limit, limit);
        }
        return newsList;
    }

    /**
     * 资讯板块的封面（最新发布）
     * @return 新闻集合
     */
    public List<News> getCoverList() {
        return newsDao.getCoverList();
    }


    /**
     * 获取用户收藏的所有资讯
     * @param newsIds 资讯ID列表
     * @return
     */
    public List<News> findCollectNews(List<Integer> newsIds) {
        List<News> newsList = new ArrayList<>();
        for (Integer id : newsIds) {
            News news = newsDao.selectById(id);
            //收藏的资讯为空，说明资讯已被删除，则应当清除出用户的收藏列表中
            if (news == null){
                collectService.deleteTargetCollector(AlumniEnum.news.getAlumniType(), id, hostHolder.getUser().getId());
            }else{
                newsList.add(news);
            }
        }
        return newsList;
    }

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    public Integer getCountBySort(Integer sort) {
        if (sort == NewsEnum.all.getCode()){
            return newsDao.getCount();
        }
        return newsDao.getCountBySort(NewsEnum.fromCode(sort).getNewsType());
    }

    /**
     * 资讯检索
     * @param search 关键词
     * @return
     */
    public List<News> searchNews(String search) {
        return newsDao.searchNews(search);
    }
}
