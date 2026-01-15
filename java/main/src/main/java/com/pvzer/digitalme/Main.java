package com.pvzer.digitalme;


import com.pvzer.digitalme.controller.ChatBotController;
import com.pvzer.digitalme.controller.MobileAgentController;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
//        ChatBotController chatbot = new ChatBotController();
//        chatbot.start();

        MobileAgentController mobileAgent = new MobileAgentController();
        Scanner scanner = new Scanner((System.in));

        while(true){
            System.out.println("请输入你要做的事：");
            mobileAgent.start(scanner.nextLine());
        }
    }

}
