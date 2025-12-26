package com.nefu.practice.serivce;


import com.nefu.practice.entity.Image;
import com.nefu.practice.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.image-dir}")
    private String IMAGE_DIR;


//    private static final String IMAGE_DIR = "data/images/";


    @Autowired
    private ImageRepository imageRepository;

    /**
     * 保存影像
     */
    public String upload(MultipartFile file, String name, String remark) {
        try {
            // 1. 生成 ID
            String imageId = UUID.randomUUID().toString();

            // 2. 保存文件
            String filename = imageId + "_" + file.getOriginalFilename();
            File dest = new File(IMAGE_DIR + filename);
            if (dest.getParentFile() != null && !dest.getParentFile().exists()) {
                boolean ok = dest.getParentFile().mkdirs();
                if (!ok) {
                    throw new RuntimeException("Failed to create dir: " + dest.getParent());
                }
            }
            file.transferTo(dest);

            // 3. 保存元数据
            Image image = new Image();
            image.setId(imageId);
            image.setName(name != null ? name : file.getOriginalFilename());
            image.setFilePath(dest.getAbsolutePath());
            image.setFileSize(file.getSize());
            image.setUploadTime(LocalDateTime.now());
            image.setRemark(remark);

            imageRepository.save(image);
            return imageId;

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }

    }
    /**
     * 查询影像列表（按上传时间倒序）
     */
    public List<Image> list() {
        return imageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadTime"));
    }

    /**
     * 根据 ID 获取影像
     */
    public Image get(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public String getImagePath(String imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
        return image.getFilePath();
    }
}
