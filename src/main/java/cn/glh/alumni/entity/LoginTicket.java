package cn.glh.alumni.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Administrator
 * @Date: 2022/2/6 12:05
 * Description 登录凭证
 */
@Data
public class LoginTicket {

    private int id;
    private int userId;

    /**
     * 凭证
     */
    private String ticket;

    /**
     * 状态（是否有效）
     */
    private boolean status;

    /**
     * 过期时间
     */
    private Date expired;
}
