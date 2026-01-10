package com.pvzer.digitalme;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        AdbTool adbTool = new AdbTool().inputTap(540,2216);

        BufferedImage image = FileTool.cropImage(adbTool.screenCapToMemory(),200,0,500,500);

        int count = 1;
        while(true) {

            BufferedImage image2 = FileTool.cropImage(adbTool.screenCapToMemory(), 200, 0, 500, 500);

            if (FileTool.hasChanged(image, image2)) {
                System.out.println("第" + count++ + "轮新的消息");

                Thread.sleep(3000);

                image = FileTool.cropImage(adbTool.screenCapToMemory(), 200, 0, 500, 500);

                String imgsrc = FileTool.aggressiveCompressToBase64(adbTool.screenCapToMemory());

                ModuleRequest ai = new ModuleRequest();
                String result = ai.askModel(imgsrc, "现在你是一个微信好友，我会发送一个聊天记录截图，你将是绿色消息的一方，也就是绿色背景文字的发送者。请你什么其他内容都不要说，诸如“好的、我知道了”，“作为一个AI”之类的绝对杜绝，需要一个绝对简短的话，随机1-4条数组形式，不能包含其他任何字符，回答样例：[]，如果你发现最后的消息是你发的，也就是最后的消息是绿色泡泡的，你就什么都别响应，响应一个“[]”就行，除非看起来确实是没说完事情，完全按照要求，完全与上下文接轨，但也不要太骚，重在看上下文，不要只看最后一两条消息，要结合全文去推敲，通过备注推断关系熟悉程度，才能符合要求。你不要深度思考，最快响应，你就是绿色泡泡的，你就是在和对方聊天。");

                System.out.println("AI回复："+result);
                ObjectMapper mapper = new ObjectMapper();

                String[] results = mapper.readValue(result, String[].class);

                for (String text : results) {
                    adbTool
                            .inputText(text)
                            .inputTap(972, 2137)
                            .sleep(1000);
                }

            }

        }
//
        }


    }
