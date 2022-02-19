package cn.glh.alumni.controller;

import cn.glh.alumni.base.Result;
import cn.glh.alumni.util.AlumniUtil;
import cn.glh.alumni.util.QiniuUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: Administrator
 * @Date: 2022/2/8 20:48
 * Description
 */
@RestController
@RequestMapping("/api")
public class UploadController {

    @PostMapping(value = "/upload")
    public String upload(@RequestParam("file")MultipartFile file){
        try {
            String imgUrl = QiniuUtils.upload(file.getBytes(), file.getOriginalFilename());
            Map<String, Object> map = new HashMap<>();
            map.put("src", imgUrl);
            return AlumniUtil.getJSONString(0,"上传成功",map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AlumniUtil.getJSONString(1,"上传失败");
    }
}
