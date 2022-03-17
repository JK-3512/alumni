package cn.glh.alumni.entity;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * 校友表(User)实体类
 *
 * @author Administrator
 * @since 2022-02-05 16:23:00
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -87879006455990321L;

    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码(加密后)
     */
    private String pwd;

    /**
     * 加盐
     */
    private String salt;

    /**
     * 性别(0.男 1.女)
     */
    private Integer sex;
    /**
     * 头像地址
     */
    private String headPic;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 籍贯(省级)
     */
    private String province;

    /**
     * 籍贯(市级)
     */
    private String city;

    /**
     * 专业
     */
    private String major;
    /**
     * 在校时间(年范围)
     */
    private String schoolDate;
    /**
     * 学历
     */
    private String education;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机
     */
    private String phone;
    /**
     * 状态(0.未激活 1.激活)
     */
    private Boolean state;
    /**
     * 0-普通用户; 1-管理员;
     */
    private Integer type;
    /**
     * 激活码
     */
    private String activationCode;
    /**
     * 创建时间
     */
    private Date createTime;

}

