package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 帖子表(Post)实体类
 *
 * @author makejava
 * @since 2022-02-28 09:28:22
 */
@Data
public class Post implements Serializable {
    private static final long serialVersionUID = 135544746413099115L;

    private Integer id;

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
     * 作者
     */
    private String author;

}

