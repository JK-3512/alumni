package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 评论回复表(Comment)实体类
 *
 * @author Administrator
 * @since 2022-02-14 09:45:40
 */
@Data
public class Comment implements Serializable {
    private static final long serialVersionUID = 140217730785400621L;

    private Integer id;
    /**
     * 目标类型:具体哪个业务
     */
    private Integer targetType;
    /**
     * 目标ID:具体哪个业务对应ID
     */
    private Integer targetId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论用户ID
     */
    private Integer fromUserId;
    /**
     * 评论目标用户id
     */
    private Integer toUserId;
    /**
     * 0.禁用 1.正常
     */
    private Boolean state;
    /**
     * 创建时间
     */
    private Date createTime;

}

