package com.pvzer.digitalme;

import java.io.*;

public class AdbTool implements AutoCloseable {
    private final Process process;
    private final BufferedWriter writer;

    public AdbTool() throws IOException {
        // 启动一个持久的 shell 进程
        this.process = new ProcessBuilder("adb", "shell").start();
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    // 往同一个 Shell 会话里发送命令
    public AdbTool exec(String command) throws IOException {
        writer.write(command);
        writer.newLine(); // 相当于敲回车
        writer.flush();   // 确保命令发送出去了
        return this;
    }

    public AdbTool sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public AdbTool inputText(String text) throws IOException {
        // 在同一个 shell 里执行
        return exec("am broadcast -a ADB_INPUT_TEXT --es msg '" + text + "'");
    }

    public AdbTool inputTap(int x,int y) throws IOException {
        return exec("input tap "+x+" "+y);
    }

    @Override
    public void close() throws IOException {
        writer.write("exit"); // 退出手机 shell
        writer.newLine();
        writer.flush();
        process.destroy();
    }


}