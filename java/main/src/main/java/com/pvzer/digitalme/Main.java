package com.pvzer.digitalme;


import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        AdbTool adbTool = new AdbTool();
        while(true){
            System.out.print("请输入要发送的话：");

            Scanner scanner = new Scanner(System.in);
            String text = scanner.next();

                    adbTool
                            .inputTap(492,2216)
                            .inputText(text)
                            .inputTap(972,2137);

        }


    }
}