package com.nefu.practice.serivce.worker;

import com.nefu.practice.entity.Task;
import com.nefu.practice.serivce.ImageService;
import com.nefu.practice.serivce.TaskService;
import com.nefu.practice.util.ImageDiffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ChangeDetectionWorker {

    @Value("${file.result-dir}")
    private String RESULT_DIR;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TaskService taskService;

    public void process(String taskId) {
        try {
            // 标记为运行中
            taskService.markRunning(taskId);

            // 阶段一 读取影像文件

            // 获取影像id
            Task task = taskService.get(taskId);
            String baseImageId = task.getBaseImageId();
            String targetImageId = task.getTargetImageId();

            // 获取影像路径
            String baseImagePath = imageService.getImagePath(baseImageId);
            String targetImagePath = imageService.getImagePath(targetImageId);
            taskService.updateProgress(taskId, 20);

            // 计算mask图片
            String maskPath = ImageDiffUtil.detectAndGenerateMask(taskId, baseImagePath, targetImagePath, RESULT_DIR);

            taskService.updateProgress(taskId, 80);

            // 5. 完成任务
            taskService.markSuccess(taskId, maskPath);

        } catch (Exception e) {
            taskService.markError(taskId);
        }
    }

    private String generateMask(String taskId, int width, int height) throws IOException {
        // 1. 创建灰度图
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = mask.createGraphics();

        // 2. 全黑（未变化）
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // 3. 模拟变化区域（白色矩形）
        g.setColor(Color.WHITE);
        g.fillRect(width / 4, height / 4, width / 3, height / 3);

        g.dispose();

        // 4. 确保目录存在
        File dir = new File(RESULT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 5. 写入文件
        File outFile = new File(dir, taskId + "_mask.png");
        ImageIO.write(mask, "png", outFile);

        return outFile.getAbsolutePath();
    }
}
