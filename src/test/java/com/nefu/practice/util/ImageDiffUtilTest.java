package com.nefu.practice.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageDiffUtilTest {

    @Test
    void detectAndGenerateMask_shouldGenerateOverlayPngAndMaskBmp() throws Exception {
        Path dir = Files.createTempDirectory("cd-test-");
        String taskId = "taskX";

        Path base = dir.resolve("base.bmp");
        Path target = dir.resolve("target.bmp");

        // 生成两张 10x10 BMP：target 在 (5,5) 处改一个像素
        BufferedImage img1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        ImageIO.write(img1, "bmp", base.toFile());

        img2.setRGB(5, 5, 0xFFFFFF);
        ImageIO.write(img2, "bmp", target.toFile());

        String resultPath = ImageDiffUtil.detectAndGenerateMask(taskId, base.toString(), target.toString(), dir.toString());

        assertNotNull(resultPath);
        assertTrue(resultPath.endsWith("_overlay.png"));
        assertTrue(new File(dir.toFile(), taskId + "_overlay.png").exists());
        assertTrue(new File(dir.toFile(), taskId + "_mask.bmp").exists());
    }
}

