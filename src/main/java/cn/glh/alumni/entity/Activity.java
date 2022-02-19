package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 活动表(Activity)实体类
 *
 * @author Administrator
 * @since 2022-02-09 14:54:20
 */
@Data
public class Activity implements Serializable {
    private static final long serialVersionUID = -69532910408055483L;

    private Integer id;
    /**
     * 分类
     */
    private String sort;
    /**
     * 封面
     */
    private String cover;
    /**
     * 标题
     */
    private String title;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 报名截止日期
     */
    private String closeDate;
    /**
     * 人数上限
     */
    private Integer numberLimit;
    /**
     * 内容
     */
    private String content;
    /**
     * 点赞
     */
    private Integer star;
    /**
     * 收藏
     */
    private Integer collect;
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
     * 活动开始日期
     */
    private String startDate;
    /**
     * 活动结束日期
     */
    private String endDate;
    /**
     * 活动地点(省级)
     */
    private String province;
    /**
     * 活动地点(市级)
     */
    private String city;
    /**
     * 活动地点(区县级)
     */
    private String county;
    /**
     * 详细地点
     */
    private String address;

}

