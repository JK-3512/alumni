package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Administrator
 * @Date: 2022/2/14 10:04
 * Description 评论/回复所在类型
 */
@Getter
@AllArgsConstructor
public enum AlumniEnum {

    //新闻资讯
    news(1,"news"),
    //活动
    activity(2,"activity"),
    //相册
    album(3,"album"),
    //帖子
    post(4,"post"),
    //评论/回复
    comment(5, "comment");

    private final int code;
    private final String name;


    private static final Map<Integer, AlumniEnum> KEY_MAP = new HashMap<>();

    static {
        for (AlumniEnum item : AlumniEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static AlumniEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getAlumniType() {
        return this.name;
    }


}
