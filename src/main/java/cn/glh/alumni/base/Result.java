package cn.glh.alumni.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于封装返回给前端的结果
 *
 * @author Administrator
 * @date 2022-02-04 11:53:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result fail(Integer code, String msg) {
        return new Result<>(code, msg);
    }

    public static Result ok() {
        SystemCode systemCode = SystemCode.OK;
        return new Result<>(systemCode.getCode(), systemCode.getMessage());
    }

    public static <F> Result<F> ok(F data) {
        SystemCode systemCode = SystemCode.OK;
        return new Result<>(systemCode.getCode(), systemCode.getMessage(), data);
    }
}
