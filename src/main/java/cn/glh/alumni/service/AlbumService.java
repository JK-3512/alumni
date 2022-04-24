package cn.glh.alumni.service;

import cn.glh.alumni.dao.UserDao;
import cn.glh.alumni.entity.Activity;
import cn.glh.alumni.entity.Album;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.AlbumDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.entity.enums.ActivityEnum;
import cn.glh.alumni.entity.enums.AlbumEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 相册表(Album)表服务实现类
 *
 * @author makejava
 * @since 2022-02-20 17:21:00
 */
@Service
public class AlbumService {
    @Resource
    private AlbumDao albumDao;

    @Resource
    private UserDao userDao;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 通过ID查询单条数据
     * @param id 主键
     * @return 实例对象
     */
    public Album selectById(Integer id) {
        Album album = this.albumDao.selectById(id);
        List<String> albumPicList = this.albumDao.getAlbumPicList(id);
        album.setAlbumPicList(albumPicList);
        return album;
    }

    /**
     * 新增数据
     * @param album 实例对象
     * @return 实例对象
     */
    public int insert(Album album) {
        return this.albumDao.insertAlbum(album);
    }

    /**
     * 修改数据
     * @param album 实例对象
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateAlbum(Album album) {
        //先删除相册下所有原有图片,再重新插入
        if (album.getAlbumPicList() != null){
            this.albumDao.deleteAlbumPic(album.getId());
            this.albumDao.insertAlbumPic(album.getId(), album.getAlbumPicList());
        }
        //重新设置相册类别、状态
        album.setSort(AlbumEnum.fromCode(Integer.valueOf(album.getSort())).getAlbumSort());
        album.setState(0);
        int i = this.albumDao.update(album);

        User user = hostHolder.getUser();
        //发布事件
        UserEventLog userEventLog = new UserEventLog(user.getId(), "更新了相册:"+ album.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
        return i;
    }

    /**
     * 通过主键删除数据,删除相册对应下的评论回复及点赞、收藏状态
     * @param id 主键
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Integer id) {
        //生成动态
        Album album = this.selectById(id);
        //事件产生
        UserEventLog userEventLog = new UserEventLog(hostHolder.getUser().getId(),  "删除了相册:"+ album.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));

        //清除reids缓存中该相册的点赞数据
        likeService.deleteTargetLike(AlumniEnum.album.getAlumniType(), id);
        //清除reids缓存中该相册的收藏数据
        collectService.deleteTargetCollect(AlumniEnum.album.getAlumniType(), id);

        //清除相册对应的所有图片(外键约束)
        this.albumDao.deleteAlbumPic(id);
        //清除该相册的评论及回复
        commentService.deleteComment(AlumniEnum.album.getCode(), id);
        //清除相册
        return this.albumDao.deleteById(id);
    }

    /**
     * 发布相册
     * @param album 相册对象
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertAlbum(Album album) {
        if (album == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        User user = hostHolder.getUser();

        //设置分类、时间、状态、校友ID、
        album.setSort(AlbumEnum.fromCode(Integer.valueOf(album.getSort())).getAlbumSort());
        album.setCreateTime(new Date());
        album.setState(0);
        album.setUserId(user.getId());
        int i = albumDao.insertAlbum(album);

        //插入多图片
        albumDao.insertAlbumPic(album.getId(), album.getAlbumPicList());

        //发布事件
        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(), user.getUserName() + "发布了相册:"+ album.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
        return i;
    }

    /**
     * 相册列表
     * @param sort 分类
     * @param page 分页参数(当前页码)
     * @param limit 分页参数(数据量)
     * @return 相册对象集合
     */
    public List<Album> getAlbumList(Integer sort, Integer page, Integer limit) {
        List<Album> albumList = null;
        //全部查询
        if (sort == AlbumEnum.all.getCode()){
            albumList = albumDao.findAllAlbum((page - 1) * limit, limit);
        }else {
            albumList = albumDao.findAlbumBySort(AlbumEnum.fromCode(sort).getAlbumSort(), (page - 1) * limit, limit);
        }
        //为每一个查询出的相册对象赋值图片
        for (Album album : albumList) {
            List<String> albumPicList = albumDao.getAlbumPicList(album.getId());
            album.setAlbumPicList(albumPicList);
        }
        return albumList;
    }

    /**
     * 获取相册下属所有的图片
     * @param id 相册ID
     * @return 图片集合
     */
    public List<String> getAlbumPicList(Integer id) {
        return albumDao.getAlbumPicList(id);
    }

    /**
     * 找出用户发布的相册
     * @param userId
     * @return
     */
    public List<Album> findByUserId(Integer userId) {
        List<Album> albumList = albumDao.findByUserId(userId);
        for (Album album : albumList) {
            List<String> albumPicList = albumDao.getAlbumPicList(album.getId());
            album.setAlbumPicList(albumPicList);
        }
        return albumList;
    }

    /**
     * 获取用户收藏的所有相册
     * @param albumIds 相册ID列表
     * @return
     */
    public List<Album> findCollectAlbum(List<Integer> albumIds) {
        List<Album> albumList = new ArrayList<>();
        for (Integer id : albumIds) {
            Album album = albumDao.selectById(id);
            //收藏的相册为空，说明相册已被删除，则应当清除出用户的收藏列表中
            if (album == null){
                collectService.deleteTargetCollector(AlumniEnum.album.getAlumniType(), id, hostHolder.getUser().getId());
            }else{
                album.setAuthor(userDao.selectById(album.getUserId()).getNickName());
                //为每一个查询出的相册对象赋值图片
                List<String> albumPicList = albumDao.getAlbumPicList(id);
                album.setAlbumPicList(albumPicList);
                albumList.add(album);
            }
        }
        return albumList;
    }

    /**
     * 分页查询
     * @param page 页数
     * @param limit 每页最大记录数
     * @return
     */
    public List<Album> queryByPage(Integer page, Integer limit) {
        List<Album> albumList = albumDao.queryByPage((page - 1) * limit, limit);
        for (Album album : albumList) {
            //为每一个查询出的相册对象赋值图片
            List<String> albumPicList = albumDao.getAlbumPicList(album.getId());
            album.setAlbumPicList(albumPicList);
        }
        return albumList;
    }

    /**
     * 分页查询未审核信息
     * @param page 页数
     * @param limit 每页最大记录数
     * @return
     */
    public List<Album> queryAuditByPage(Integer page, Integer limit) {
        List<Album> albumList = albumDao.queryAuditByPage((page - 1) * limit, limit);
        for (Album album : albumList) {
            //为每一个查询出的相册对象赋值图片
            List<String> albumPicList = albumDao.getAlbumPicList(album.getId());
            album.setAlbumPicList(albumPicList);
        }
        return albumList;
    }

    /**
     * 修改活动审核状态
     * @param id 资讯ID
     * @param state 0.未审核 1.审核通过 2.审核不通过
     * @return 影响行数
     */
    public int updateState(Integer id, Integer state){
        return albumDao.updateState(id, state);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStateList(List<Integer> ids, Integer state){
        for (Integer id : ids) {
            this.updateState(id, state);
        }
    }

    /**
     * 为相册模块添加封面
     * @return 最新发布的相册
     */
    public Album getNewAlbum() {
        Album newAlbum = albumDao.getNewAlbum();
        newAlbum.setAlbumPicList(albumDao.getAlbumPicList(newAlbum.getId()));
        return newAlbum;
    }

    /**
     * 获取该类别下的总条码
     * @param sort 类别
     * @return
     */
    public Integer getCountBySort(Integer sort) {
        if (sort == AlbumEnum.all.getCode()){
            return albumDao.getCount();
        }
        return albumDao.getCountBySort(AlbumEnum.fromCode(sort).getAlbumSort());
    }

    /**
     * 相册检索
     * @param search 关键词
     * @return
     */
    public List<Album> searchAlbum(String search) {
        List<Album> albumList = albumDao.searchAlbum(search);
        for (Album album : albumList) {
            album.setAlbumPicList(albumDao.getAlbumPicList(album.getId()));
        }
        return albumList;
    }
}
