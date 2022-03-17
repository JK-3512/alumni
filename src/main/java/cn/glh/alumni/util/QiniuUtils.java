package cn.glh.alumni.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Administrator
 * @Date: 2022/2/8 19:26
 * Description 七牛云工具类
 */
public class QiniuUtils {

    private static final String ACCESS_KEY = "EJR3oiw6SZh1FhwFp96KDuJmF8QHUeFfW-7UmzbB";
    private static final String SECRET_KEY = "Q3u6SyqI3IxvPjg-XJyVxHFfYaa32UK5rCaOFGKU";
    private static final String BUCKET = "alumni-glh";
    private static final String BUCKET_URL = "http://r8tp3srqc.hn-bkt.clouddn.com";

    private static Configuration cfg;
    private static UploadManager uploadManager;
    private static Auth auth;
    private static String upToken;

    private static void  init(){
        cfg = new Configuration(Region.huanan());
        uploadManager = new UploadManager(cfg);
        auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        upToken = auth.uploadToken(BUCKET);
    }

    /**
     * 字节数组上传 可以支持将内存中的字节数组上传到空间中。
     *
     * @param bytes
     * @param fileName
     * @return Map
     */
    public static String upload(byte[] bytes, String fileName) {
        init();
        String randomImgName = getRandomImgName(fileName);
        try {
            Response response = uploadManager.put(bytes, randomImgName, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return BUCKET_URL + "/" + putRet.key;
        } catch (QiniuException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     *删除空间中的文件
     * @param fileName
     */
    public static void deleteFile(String fileName) {
        init();
        BucketManager bucketManager = new BucketManager(auth, cfg);
        String randomImgName = getRandomImgName(fileName);
        try {
            bucketManager.delete(BUCKET, randomImgName);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

    /**
     * 重置图片名称
     * @param fileName
     * @return
     */
    private static String getRandomImgName(String fileName) {
        int index = fileName.lastIndexOf(".");
        if ((fileName == null || fileName.isEmpty()) || index == -1){
            throw new IllegalArgumentException();
        }
        // 获取文件后缀
        String suffix = fileName.substring(index);
        // 生成UUID
        String uuid = AlumniUtil.generateUUID();
        //生成日期
        String today = AlumniUtil.today();
        // 返回随机名称
        return today + "-" + uuid + suffix;
    }
}
