package com.pvzer.digitalme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvzer.digitalme.consistant.Value;
import com.pvzer.digitalme.service.AdbService;
import com.pvzer.digitalme.service.FileService;
import com.pvzer.digitalme.service.ModuleRequestService;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class MobileAgentController {
    private String prompt;
    private String rule;

    private int sendX = Value.wxSendX;
    private int sendY = Value.wxSendY;

    public void start() throws Exception {

        AdbService adbTool = new AdbService();
        System.out.print("输入你要解决的问题：");
        Scanner sc = new Scanner(System.in);

        rule = Files.readString(Path.of("prompts/prompt.txt"));
        prompt = Files.readString(Path.of("prompts/"+sc.nextLine()+".txt"));


        BufferedImage image = new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);

        int count = 1;

        while(true) {

            BufferedImage image2 = FileService.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);;

            if (FileService.hasChanged(image, image2)) {
                System.out.println("第" + count++ + "轮新的消息");

                image = FileService.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);

                Thread.sleep(3000);

                String imgsrc = FileService.aggressiveCompressToBase64(adbTool.screenCapToMemory());
                ModuleRequestService ai = new ModuleRequestService();
                String result = ai.askModel(imgsrc,  rule+prompt)+"\n";

//                System.out.println(result);

                ObjectMapper mapper = new ObjectMapper();

                String[] results = mapper.readValue(result, String[].class);

                for (int i = 0;i < results.length;i++) {
                    System.out.println(results[i]);
                    adbTool
                            .inputText(results[i])
                            .sleep(results[i].length()* 250L)
                            .inputTap(sendX, sendY);
                }

                Files.write(
                        Paths.get("history.log"),
                        result.getBytes(StandardCharsets.UTF_8), // 指定UTF-8编码，避免乱码
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE
                );

                Thread.sleep(500);
                image = FileService.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);

            }

        }
    }
}
