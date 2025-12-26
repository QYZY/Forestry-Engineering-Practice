package com.nefu.practice.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图像变化检测工具类（BMP-only）
 * 算法：像素级 RGB 差分
 * 输出：二值 BMP Mask
 */
public class ImageDiffUtil {

    /** 差分阈值（经验值，课程项目完全可接受） */
    private static final int DIFF_THRESHOLD = 40;

    /**
     * 执行变化检测并生成 BMP Mask
     *
     * @param taskId          任务ID
     * @param baseImagePath   前期影像路径（BMP）
     * @param targetImagePath 后期影像路径（BMP）
     * @param outputDir       Mask 输出目录
     * @return Mask BMP 的绝对路径
     */
    public static String detectAndGenerateMask(
            String taskId,
            String baseImagePath,
            String targetImagePath,
            String outputDir
    ) {

        try {
            /* ===== 1. 读取 BMP ===== */
            BufferedImage baseImage = ImageIO.read(new File(baseImagePath));
            BufferedImage targetImage = ImageIO.read(new File(targetImagePath));

            if (baseImage == null || targetImage == null) {
                throw new RuntimeException("Failed to read BMP image");
            }

            /* ===== 2. 尺寸校验 ===== */
            int width = baseImage.getWidth();
            int height = baseImage.getHeight();

            if (width != targetImage.getWidth()
                    || height != targetImage.getHeight()) {
                throw new RuntimeException("Image size mismatch");
            }

            /* ===== 3. 创建 Mask 图像 ===== */
            BufferedImage mask = new BufferedImage(
                    width,
                    height,
                    BufferedImage.TYPE_BYTE_GRAY
            );

            /* ===== 4. 像素级变化检测 ===== */
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    int rgb1 = baseImage.getRGB(x, y);
                    int rgb2 = targetImage.getRGB(x, y);

                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >> 8) & 0xff;
                    int b1 = rgb1 & 0xff;

                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >> 8) & 0xff;
                    int b2 = rgb2 & 0xff;

                    int diff = Math.abs(r1 - r2)
                            + Math.abs(g1 - g2)
                            + Math.abs(b1 - b2);

                    // 白色：变化；黑色：未变化
                    mask.getRaster().setSample(
                            x, y, 0,
                            diff > DIFF_THRESHOLD ? 255 : 0
                    );
                }
            }

            /* ===== 5. 输出 BMP ===== */
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File outFile = new File(dir, taskId + "_mask.bmp");
            ImageIO.write(mask, "bmp", outFile);

            return outFile.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException("Image diff failed", e);
        }
    }
}
