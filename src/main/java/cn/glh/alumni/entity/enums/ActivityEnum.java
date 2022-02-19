package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Administrator
 * @Date: 2022/2/12 16:25
 * Description 活动分类枚举类
 */
@Getter
@AllArgsConstructor
public enum ActivityEnum {

    //全部
    all(0,null),
    //闲聊聚餐
    gossip_dinner(1,"闲聊聚餐"),
    //公益活动
    charity_campaign(2,"公益活动"),
    //校友聚会
    alumni_reunion(3,"校友聚会"),
    //旅游度假
    tourism_vacation(4,"旅游度假"),
    other(5,"其它");

    private final int code;
    private final String name;


    private static final Map<Integer, ActivityEnum> KEY_MAP = new HashMap<>();

    static {
        for (ActivityEnum item : ActivityEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static ActivityEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getActivitySort() {
        return this.name;
    }


}
