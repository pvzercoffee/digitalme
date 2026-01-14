package com.pvzer.digitalme;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Main {

    private static String prompt;
    private static String rule;


    private static int sendX = Value.wxSendX;
    private static int sendY = Value.wxSendY;

    public static void main(String[] args) throws Exception {

        AdbTool adbTool = new AdbTool();
        System.out.print("选择人格：");
        Scanner sc = new Scanner(System.in);

        rule = Files.readString(Path.of("prompts/prompt.txt"));
        prompt = Files.readString(Path.of("prompts/"+sc.next()+".txt"));

        BufferedImage image = new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);

        int count = 1;

        while(true) {

            BufferedImage image2 = FileTool.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);;

            if (FileTool.hasChanged(image, image2)) {
                System.out.println("第" + count++ + "轮新的消息");

                image = FileTool.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);

                Thread.sleep(3000);

                String imgsrc = FileTool.aggressiveCompressToBase64(adbTool.screenCapToMemory());
                ModuleRequest ai = new ModuleRequest();
                String result = ai.askModel(imgsrc,  rule+prompt)+"\n";

                System.out.println(result);

                ObjectMapper mapper = new ObjectMapper();

                String[] results = mapper.readValue(result, String[].class);

                for (int i = 0;i < results.length;i++) {
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
                image = FileTool.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);

            }

        }
//
        }


    }
