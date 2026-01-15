package com.pvzer.digitalme;


import com.pvzer.digitalme.controller.ChatBotController;

public class Main {

    public static void main(String[] args) throws Exception {
        ChatBotController chatbot = new ChatBotController();
        chatbot.start();
    }

}
