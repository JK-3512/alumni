package cn.glh.alumni.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Administrator
 * @Date: 2022/2/10 15:32
 * Description
 */
@Data
@NoArgsConstructor
public class UserEventLog implements Serializable {

    private static final long serialVersionUID = -4884205059763498105L;

    private Integer id;

    private Integer userId;

    /**
     * 动态
     */
    private String content;

    private Date createTime;

    public UserEventLog(Integer userId, String content, Date createTime) {
        this.userId = userId;
        this.content = content;
        this.createTime = createTime;
    }
}
