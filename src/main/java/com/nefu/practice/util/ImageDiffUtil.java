package com.nefu.practice.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图像变化检测工具类（BMP-only）
 * 算法：像素级 RGB 差分 + 后处理
 * 输出：
 * - 原始二值 BMP Mask（0/255灰度）
 * - 美观的变化叠加 PNG（红色半透明叠加在目标影像上）
 */
public class ImageDiffUtil {

    /** 差分阈值（经验值） */
    private static final int DIFF_THRESHOLD = 40;

    /** 后处理：闭运算半径（越大越“平滑”，但会吞掉细节） */
    private static final int MORPH_RADIUS = 1; // 1~2 一般足够

    /** 叠加图变化区域颜色 */
    private static final Color OVERLAY_COLOR = new Color(255, 0, 0);

    /** 叠加图透明度 */
    private static final float OVERLAY_ALPHA = 0.35f;

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

            if (width != targetImage.getWidth() || height != targetImage.getHeight()) {
                throw new RuntimeException("Image size mismatch");
            }

            /* ===== 3. 生成二值 mask（boolean） ===== */
            boolean[][] change = new boolean[height][width];

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

                    int diff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                    change[y][x] = diff > DIFF_THRESHOLD;
                }
            }

            /* ===== 4. 后处理（闭运算：膨胀->腐蚀）让结果更“块状”、更美观 ===== */
            boolean[][] dilated = dilate(change, width, height, MORPH_RADIUS);
            boolean[][] closed = erode(dilated, width, height, MORPH_RADIUS);

            /* ===== 5. 输出目录 ===== */
            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create dir: " + dir.getAbsolutePath());
            }

            /* ===== 6. 输出原始灰度 BMP（保留）===== */
            BufferedImage maskBmp = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    maskBmp.getRaster().setSample(x, y, 0, closed[y][x] ? 255 : 0);
                }
            }
            File outBmp = new File(dir, taskId + "_mask.bmp");
            ImageIO.write(maskBmp, "bmp", outBmp);

            /* ===== 7. 生成叠加 PNG（红色半透明叠加在 target 上）===== */
            BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = overlay.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 先铺底：目标影像
            g.drawImage(targetImage, 0, 0, null);

            // 再叠加：变化区域
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OVERLAY_ALPHA));
            g.setColor(OVERLAY_COLOR);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (closed[y][x]) {
                        g.fillRect(x, y, 1, 1);
                    }
                }
            }
            g.dispose();

            File outPng = new File(dir, taskId + "_overlay.png");
            ImageIO.write(overlay, "png", outPng);

            // 返回更美观的叠加图路径（前端展示优先）
            return outPng.getAbsolutePath();

        } catch (IOException e) {
            throw new RuntimeException("Image diff failed", e);
        }
    }

    private static boolean[][] dilate(boolean[][] src, int width, int height, int r) {
        if (r <= 0) return src;
        boolean[][] dst = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean on = false;
                for (int dy = -r; dy <= r && !on; dy++) {
                    int yy = y + dy;
                    if (yy < 0 || yy >= height) continue;
                    for (int dx = -r; dx <= r; dx++) {
                        int xx = x + dx;
                        if (xx < 0 || xx >= width) continue;
                        if (src[yy][xx]) { on = true; break; }
                    }
                }
                dst[y][x] = on;
            }
        }
        return dst;
    }

    private static boolean[][] erode(boolean[][] src, int width, int height, int r) {
        if (r <= 0) return src;
        boolean[][] dst = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean on = true;
                for (int dy = -r; dy <= r && on; dy++) {
                    int yy = y + dy;
                    if (yy < 0 || yy >= height) { on = false; break; }
                    for (int dx = -r; dx <= r; dx++) {
                        int xx = x + dx;
                        if (xx < 0 || xx >= width) { on = false; break; }
                        if (!src[yy][xx]) { on = false; break; }
                    }
                }
                dst[y][x] = on;
            }
        }
        return dst;
    }
}
