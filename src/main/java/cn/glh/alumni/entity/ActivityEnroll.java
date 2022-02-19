package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动登记表(ActivityEnroll)实体类
 *
 * @author Administrator
 * @since 2022-02-12 17:49:09
 */
@Data
public class ActivityEnroll implements Serializable {
    private static final long serialVersionUID = -39935969342461888L;
    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 校友ID
     */
    private Integer userId;
    /**
     * 校友名称
     */
    private String userName;

    /**
     * 校友头像
     */
    private String headPic;
    /**
     * 报名时间
     */
    private Date enrollTime;
}

