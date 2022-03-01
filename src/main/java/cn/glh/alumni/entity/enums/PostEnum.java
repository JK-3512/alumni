package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum PostEnum {

    //全部
    all(0,"全部"),
    //今日新鲜事
    fresh(1,"今日新鲜事"),
    //今天学到了
    learn(2,"今天学到了"),
    //下班去哪玩
    play(3,"下班去哪玩"),
    //什么值得买
    buy(4,"什么值得买"),
    //搞笑段子
    funny(5, "搞笑段子"),
    //其它
    other(6,"其它");

    private final int code;
    private final String name;


    private static final Map<Integer, PostEnum> KEY_MAP = new HashMap<>();

    static {
        for (PostEnum item : PostEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static PostEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getPostType() {
        return this.name;
    }
}
