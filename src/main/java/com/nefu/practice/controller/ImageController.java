package com.nefu.practice.controller;

import com.nefu.practice.common.Result;
import com.nefu.practice.entity.Image;
import com.nefu.practice.pojo.ImageResponse;
import com.nefu.practice.pojo.ImageUploadResponse;
import com.nefu.practice.serivce.ImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * 上传图像
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String remark
    ) {

        if (file.isEmpty()) {
            return Result.error("file is empty");
        }

        try {
            String imageId = imageService.upload(file, name, remark);
            ImageUploadResponse response = new ImageUploadResponse();
            response.setImageId(imageId);
            return Result.success(
                    "upload success",
                    response
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Result.error("upload failed");
        }
    }

    @GetMapping("/list")
    public Result<List<ImageResponse>> list() {
        List<Image> list = imageService.list();
        ArrayList<ImageResponse> imageResponses = new ArrayList<>();
        for (Image image : list) {
            ImageResponse imageResponse = new ImageResponse();
            BeanUtils.copyProperties(image, imageResponse);
            imageResponses.add(imageResponse);
        }
        return Result.success(imageResponses);
    }
}
