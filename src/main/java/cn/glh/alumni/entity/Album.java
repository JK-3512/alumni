package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 相册表(Album)实体类
 *
 * @author makejava
 * @since 2022-02-20 16:01:51
 */
@Data
public class Album implements Serializable {
    private static final long serialVersionUID = -64651470367681285L;
    
    private Integer id;
    /**
     * 分类
     */
    private String sort;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 状态(0.待审核 1.审核通过 2.审核不通过)
     */
    private Integer state;
    /**
     * 校友ID
     */
    private Integer userId;

    /**
     * 多图片
     */
    private List<String> albumPicList;

}

