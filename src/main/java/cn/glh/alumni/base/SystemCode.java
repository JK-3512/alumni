package cn.glh.alumni.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码常量类
 *
 * @author Administrator
 * @date 2022-02-04 11:53:23
 */
@Getter
@AllArgsConstructor
public enum SystemCode {

    OK(200, "成功"),
    AccessTokenError(400, "用户登录令牌失效"),
    UNAUTHORIZED(401, "用户未登录"),
    AuthError(402, "用户名或密码错误"),
    InnerError(500, "系统内部错误"),
    ParameterValidError(501, "参数验证错误"),
    AccessDenied(502, "用户没有权限访问");

    Integer code;
    String message;
}
