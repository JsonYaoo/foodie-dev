package com.imooc.resources;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

// 标识为一个组件, 供springboot扫描
@Component
// 只读取固定前缀开头的资源
@ConfigurationProperties(prefix = "file")
// 读取文件上传资源
@PropertySource("classpath:file-upload-dev.properties")
public class FileUpload {

    private String imageUserFaceLocation;
    private String imageServerUrl;
    private String imageUserFaceMapping;

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }

    public String getImageUserFaceLocation() {
        return imageUserFaceLocation;
    }

    public void setImageUserFaceLocation(String imageUserFaceLocation) {
        this.imageUserFaceLocation = imageUserFaceLocation;
    }

    public String getImageUserFaceMapping() {
        return imageUserFaceMapping;
    }

    public void setImageUserFaceMapping(String imageUserFaceMapping) {
        this.imageUserFaceMapping = imageUserFaceMapping;
    }
}