package com.nefu.practice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件读取接口：用于前端直接预览变化检测结果。
 *
 * - GET /api/file/result/{taskId}          默认返回 overlay（优先）
 * - GET /api/file/result/{taskId}/{type}  type=overlay|mask
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.result-dir}")
    private String resultDir;

    @GetMapping("/result/{taskId}")
    public ResponseEntity<Resource> getResult(@PathVariable String taskId) {
        return getResultByType(taskId, "overlay");
    }

    @GetMapping("/result/{taskId}/{type}")
    public ResponseEntity<Resource> getResultByType(@PathVariable String taskId, @PathVariable String type) {
        // overlay: taskId_overlay.png
        // mask:    taskId_mask.bmp (优先) / taskId_mask.png

        File file;
        MediaType contentType;

        if ("overlay".equalsIgnoreCase(type)) {
            File overlay = new File(resultDir, taskId + "_overlay.png");
            if (overlay.exists() && overlay.isFile()) {
                file = overlay;
                contentType = MediaType.IMAGE_PNG;
            } else {
                // overlay 不存在时回退到 mask
                return getResultByType(taskId, "mask");
            }
        } else if ("mask".equalsIgnoreCase(type)) {
            File bmp = new File(resultDir, taskId + "_mask.bmp");
            File png = new File(resultDir, taskId + "_mask.png");

            if (bmp.exists() && bmp.isFile()) {
                file = bmp;
                contentType = MediaType.valueOf("image/bmp");
            } else if (png.exists() && png.isFile()) {
                file = png;
                contentType = MediaType.IMAGE_PNG;
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

        String encoded = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header("Content-Disposition", "inline; filename*=UTF-8''" + encoded)
                .body(new FileSystemResource(file));
    }
}
