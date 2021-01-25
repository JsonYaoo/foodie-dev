package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.resources.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("fdfs")
public class FdfsController extends FdfsBaseController {

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FdfsService fdfsService;

    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(String userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception{
        // 开始文件上传
        String path = null;
        if (file != null) {
            // 获得文件上传的文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                // 文件重命名  imooc-face.png -> ["imooc-face", "png"]
                String fileNameArr[] = fileName.split("\\.");

                // 获取文件的后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                // 限制.sh | .php脚本的上传
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg") ) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确！");
                }
//                path = fdfsService.upload(file, suffix);
                path = fdfsService.uploadOss(file, userId, suffix);
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空！");
        }

        if(StringUtils.isBlank(path)){
            return IMOOCJSONResult.errorMsg("上传头像失败！");
        }

//        String finalUserFaceUrl = fileResource.getHost() + path;
        String finalUserFaceUrl = fileResource.getOssHost() + path;
        System.out.println("图片上传路径为: " + path);

        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(conventUsersVO(userResult)), true);
        return IMOOCJSONResult.ok();
    }

}
