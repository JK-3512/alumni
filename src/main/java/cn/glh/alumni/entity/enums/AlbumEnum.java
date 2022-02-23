package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum AlbumEnum {

    //全部
    all(0,null),
    //同学时光
    class_time(1,"同学时光"),
    //校园追忆
    campus_remembrance(2,"校园追忆"),
    //校友今夕
    alumni_this_evening(3,"校友今夕"),
    //活动聚会
    event_gatherings(4,"活动聚会"),
    //个人风采
    personal_touch(5,"个人风采"),
    other(6,"其它");

    private final int code;
    private final String name;


    private static final Map<Integer, AlbumEnum> KEY_MAP = new HashMap<>();

    static {
        for (AlbumEnum item : AlbumEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static AlbumEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getAlbumSort() {
        return this.name;
    }


}
