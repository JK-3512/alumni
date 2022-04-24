package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum NewsEnum {

    all(0,"全部"),
    Alumni_information(1,"校友资讯"),
    Alumni_style(2,"校友风采"),
    Alma_mater_information(3,"母校资讯"),
    other(4,"其它");

    private final int code;
    private final String name;


    private static final Map<Integer, NewsEnum> KEY_MAP = new HashMap<>();

    static {
        for (NewsEnum item : NewsEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static NewsEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getNewsType() {
        return this.name;
    }
}
