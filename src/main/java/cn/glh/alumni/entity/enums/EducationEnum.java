package cn.glh.alumni.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum EducationEnum {

    //初中
    junior(0,"初中"),
    //高中
    high(1,"高中"),
    //专科
    specialty(2,"专科"),
    //本科
    undergraduate(3,"本科"),
    //硕士
    master(4,"硕士"),
    //博士
    doctor(5, "博士"),
    //其它
    other(6,"其它");

    private final int code;
    private final String name;


    private static final Map<Integer, EducationEnum> KEY_MAP = new HashMap<>();

    static {
        for (EducationEnum item : EducationEnum.values()) {
            KEY_MAP.put(item.getCode(), item);
        }
    }

    public static EducationEnum fromCode(Integer code) {
        return KEY_MAP.get(code);
    }

    public String getEducationType() {
        return this.name;
    }
}
