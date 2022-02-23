package cn.glh.alumni.service.impl;

import cn.glh.alumni.entity.Album;
import cn.glh.alumni.base.BasePage;
import cn.glh.alumni.dao.AlbumDao;
import cn.glh.alumni.entity.User;
import cn.glh.alumni.entity.UserEventLog;
import cn.glh.alumni.entity.enums.AlbumEnum;
import cn.glh.alumni.entity.enums.AlumniEnum;
import cn.glh.alumni.event.UserEvent;
import cn.glh.alumni.service.AlbumService;
import cn.glh.alumni.util.HostHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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

    @Resource
    private HostHolder hostHolder;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Album selectById(Integer id) {
        return this.albumDao.selectById(id);
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
        return this.albumDao.insertAlbum(album);
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

    /**
     * 发布相册
     * @param album 相册对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertAlbum(Album album) {
        if (album == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        User user = hostHolder.getUser();

        //设置分类、时间、状态、校友ID、
        album.setSort(AlbumEnum.fromCode(Integer.valueOf(album.getSort())).getAlbumSort());
        album.setCreateTime(new Date());
        album.setState(0);
        album.setUserId(user.getId());
        albumDao.insertAlbum(album);

        //插入多图片
        albumDao.insertAlbumPic(album.getId(), album.getAlbumPicList());

        //发布事件
        //事件产生
        UserEventLog userEventLog = new UserEventLog(user.getId(), user.getUserName() + "发布了相册:"+ album.getTitle(), new Date());
        //事件发布,需要listener接收到事件并处理返回
        applicationEventPublisher.publishEvent(new UserEvent(userEventLog));
    }

    /**
     * 相册列表
     * @param sort 分类
     * @return 相册对象集合
     */
    @Override
    public List<Album> getAlbumList(Integer sort) {
        List<Album> albumList = null;
        //全部查询
        if (sort == AlbumEnum.all.getCode()){
            albumList = albumDao.queryAll();
        }else {
            albumList = albumDao.getAlbumList(AlbumEnum.fromCode(sort).getAlbumSort());
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
    @Override
    public List<String> getAlbumPicList(Integer id) {
        return albumDao.getAlbumPicList(id);
    }
}
