package cn.glh.alumni.util;

/**
 * @Author: Administrator
 * @Date: 2022/2/6 11:43
 * Description 常量
 */
public class AlumniConstant {

    /**
     * 激活成功
     */
    public static final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    public static final int ACTIVATION_FAILURE = 2;

    /**
     * 默认的登录凭证超时时间 (12小时)
     */
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 权限：普通用户
     */
    public static final String AUTHORITY_USER = "ROLE_USER";

    /**
     * 权限：管理员
     */
    public static final String AUTHORITY_ADMIN = "ADMIN";

}
