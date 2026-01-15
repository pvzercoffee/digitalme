package com.pvzer.digitalme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private String rule;

    public void start(String prompt) throws Exception {

        AdbService adbTool = new AdbService();

        rule = Files.readString(Path.of("prompts/agent.txt"));


        int count = 1;
        while(true) {

            BufferedImage image2 = FileService.cropImage(adbTool.screenCapToMemory(), 200, 400, 200, 200);;

                Thread.sleep(2000);

                String imgsrc = FileService.aggressiveCompressToBase64(adbTool.screenCapToMemory());
                ModuleRequestService ai = new ModuleRequestService();
                String result = ai.askModel(imgsrc,  rule+"用户手机分辨率："+adbTool.getWmSize()+prompt)+"\n";

//                System.out.println(result);

                ObjectMapper mapper = new ObjectMapper();

                String[] results = mapper.readValue(result, String[].class);

                adbTool.shell(results[0]);
                System.out.println("第"+count+"步:"+results[1]);


                Files.write(
                        Paths.get("history.log"),
                        result.getBytes(StandardCharsets.UTF_8), // 指定UTF-8编码，避免乱码
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE
                );

                if(results[1].equals("done")) break;

                Thread.sleep(500);
                count++;

        }
    }
}
