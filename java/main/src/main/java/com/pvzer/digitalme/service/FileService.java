package com.pvzer.digitalme.service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;

public class FileService {
    public static String aggressiveCompressToBase64(BufferedImage source) throws Exception {
        // 1. 激进缩放：宽度限制在 720，文字依然清晰但像素暴减
        int targetWidth = Math.min(source.getWidth(), 720);
        int targetHeight = (int) (source.getHeight() * (targetWidth / (double) source.getWidth()));

        // 2. 灰度处理：使用 TYPE_BYTE_GRAY，丢弃颜色信息，只保留明暗，大幅缩小体积
        BufferedImage grayImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayImage.createGraphics();
        // 开启双线性插值，防止缩放后文字出现断裂
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        // 3. 极低质量 JPEG 压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        try (var ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.3f); // 激进质量：0.3
            }
            writer.write(null, new IIOImage(grayImage, null, null), param);
        } finally {
            writer.dispose();
        }

        // 4. 返回 Base64
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static String balanceCompressToBase64(BufferedImage source) throws Exception {
        // 保持原分辨率，确保按钮坐标准确
        int width = source.getWidth();
        int height = source.getHeight();

        // 转为灰度（减少2/3数据量）
        BufferedImage grayImage = new BufferedImage(
                width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayImage.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        // JPEG压缩，质量0.5（最佳平衡点）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.5f); // 关键设置！
            }

            writer.write(null, new IIOImage(grayImage, null, null), param);
        }

        byte[] compressed = baos.toByteArray();

        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(compressed);
    }

    /**
     * @param source 全屏图
     * @param x 区域左上角横坐标
     * @param y 区域左上角纵坐标
     * @param width 区域宽度
     * @param height 区域高度
     */
    public static BufferedImage cropImage(BufferedImage source, int x, int y, int width, int height) {
        // getSubimage 是浅拷贝，它共享原图的数据内存，非常快
        return source.getSubimage(x, y, width, height);
    }

    public static boolean hasChanged(BufferedImage img1, BufferedImage img2) throws Exception {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return true; // 尺寸都不一样，肯定变了
        }

        // 1. 转为字节数组 (用 JPG 会有压缩损耗，比对必须用 PNG 或 BMP)
        byte[] data1 = getImageBytes(img1);
        byte[] data2 = getImageBytes(img2);

        // 2. 直接对比数组内容
        return !Arrays.equals(data1, data2);
    }

    private static byte[] getImageBytes(BufferedImage img) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

}
