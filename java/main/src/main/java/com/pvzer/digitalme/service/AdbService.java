package com.pvzer.digitalme.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class AdbService implements AutoCloseable {
    private final Process process;
    private final BufferedWriter writer;
    private final String usePath = "/sdcard/adbfile/";

    public AdbService() throws IOException {
        // 启动一个持久的 shell 进程
        this.process = new ProcessBuilder("adb", "shell").start();
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    // 往同一个 Shell 会话里发送命令
    public AdbService exec(String command) throws IOException {
        writer.write(command);
        writer.newLine(); // 相当于敲回车
        writer.flush();   // 确保命令发送出去了
        return this;
    }

    public AdbService sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public AdbService inputText(String text) throws IOException {
        // 在同一个 shell 里执行
        return exec("am broadcast -a ADB_INPUT_TEXT --es msg '" + text + "'");
    }

    public AdbService inputTap(int x, int y) throws IOException {
        return exec("input tap "+x+" "+y);
    }

    public AdbService shell(String cmd) throws IOException {
        return exec(cmd);
    }

    public BufferedImage screenCapToMemory() throws IOException {
        // 使用 exec-out 直接获取标准输出流
        Process p = Runtime.getRuntime().exec("adb exec-out screencap  -p");

        InputStream is = p.getInputStream();
        BufferedImage img = ImageIO.read(is);

        if (img == null) {
            // 如果为 null，打印出错误流看看发生了什么
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println("ADB Error: " + line);
            }
            throw new IOException("无法读取图片流，请检查 Display 是否存在或黑屏");
        }
        return img;
    }

    @Override
    public void close() throws IOException {
        writer.write("exit"); // 退出手机 shell
        writer.newLine();
        writer.flush();
        process.destroy();
    }


}