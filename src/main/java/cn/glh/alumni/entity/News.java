package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 新闻资讯表(News)实体类
 *
 * @author makejava
 * @since 2022-03-29 14:32:22
 */
@Data
public class News implements Serializable {
    private static final long serialVersionUID = 935577958604791952L;
    
    private Integer id;
    /**
     * 类别
     */
    private String sort;
    /**
     * 标题
     */
    private String title;
    /**
     * 封面
     */
    private String cover;
    /**
     * 内容(富文本)
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
     * 管理员ID
     */
    private Integer adminId;

}

