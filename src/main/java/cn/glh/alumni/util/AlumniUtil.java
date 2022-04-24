package cn.glh.alumni.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: Administrator
 * @Date: 2022/2/5 16:02
 * Description
 */
public class AlumniUtil {

    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID() {
        // 去除生成的随机字符串中的 ”-“
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * md5 加密
     * @param key 要加密的字符串
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    /**
     * 将服务端返回的消息封装成 JSON 格式的字符串
     * @param code 状态码
     * @param msg 提示消息
     * @param map 业务数据
     * @return 返回 JSON 格式字符串
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        json.put("data", map);
//        if (map != null) {
//            for (String key : map.keySet()) {
//                map.put(key, map.get(key));
//            }
//        }
        return json.toJSONString();
    }

    /**
     * 将服务端返回的消息封装成 JSON 格式的字符串(管理员端专属)
     */
    public static String getAdminJSONString(int code, String msg,int count, List list) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        json.put("count", count);
        json.put("data", list);
        return json.toJSONString();
    }

    // 重载 getJSONString 方法，服务端方法可能不返回业务数据
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    // 重载 getJSONString 方法，服务端方法可能不返回业务数据和提示消息
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    // layuiedit uploadImg 要求返回的 JSON 字符串格式
    public static String getUploadImgJSONString(int code, String msg, String url) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        json.put("url", url);
        return json.toJSONString();
    }



    /**
     * 生成指定位数的数字随机数, 最高不超过 9 位
     *
     * @param length
     * @return
     */
    public static String getRandomCode(int length) {
        Validate.isTrue(length <= 9 && length > 0, "生成数字随机数长度范围应该在 1~9 内, 参数 length : %s", length);
        int floor = (int) Math.pow(10, length - 1);
        int codeNum = RandomUtils.nextInt(floor, floor * 10);
        return Integer.toString(codeNum);
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String today() {
        // 获取当前时间 格式 年-月-日 时:分:秒
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
    }

}
