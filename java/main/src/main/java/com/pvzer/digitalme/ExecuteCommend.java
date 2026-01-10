package com.pvzer.digitalme;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteCommend {

    public static void execute(ProcessBuilder pb){
        // 命令以字符串数组形式传入，避免路径空格问题
        pb.redirectErrorStream(true); // 合并标准输出和错误输出

        try {
            Process process = pb.start();

            // 读取命令输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("退出代码: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
