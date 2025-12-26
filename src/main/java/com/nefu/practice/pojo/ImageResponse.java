package com.nefu.practice.pojo;

import com.nefu.practice.entity.Image;
import lombok.Data;

@Data
public class ImageResponse {

    private String id;
    private String name;
    private String uploadTime;
    private String filePath;
    private String remark;

    public static ImageResponse from(Image image) {
        ImageResponse r = new ImageResponse();
        r.setId(image.getId());
        r.setName(image.getName());
        r.setUploadTime(image.getUploadTime() == null ? null : image.getUploadTime().toString());
        r.setFilePath(image.getFilePath());
        r.setRemark(image.getRemark());
        return r;
    }

}
