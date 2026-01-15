package com.pvzer.digitalme.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModuleRequestService {
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/responses";
    private static final String API_KEY = "330442d5-8430-400b-bb62-5de90a6ef055";


    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // 连接超时
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)   // 写入数据超时（传大 Base64 关键）
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // 等待 AI 响应超时
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String askModel(String imageUrl, String userText) throws IOException {

        // --- 1. 请求构造部分 ---
        ObjectNode root = mapper.createObjectNode();
        root.put("model", "doubao-seed-1-8-251228");

        ArrayNode inputArray = root.putArray("input");

        // === 新增：添加系统提示词 (System Prompt) ===
        ObjectNode systemItem = inputArray.addObject();
        systemItem.put("role", "system");
        ArrayNode systemContentArray = systemItem.putArray("content");
        ObjectNode systemTextNode = systemContentArray.addObject();
        systemTextNode.put("type", "input_text");
        systemTextNode.put("text", "rule"); // 这里必须有内容！
        // 这里放入我刚才为你优化的那段系统提示词内容

        // --- 原有的 User 消息部分 ---
        ObjectNode inputItem = inputArray.addObject();
        inputItem.put("role", "user");

        ArrayNode reqContentArray = inputItem.putArray("content");

        ObjectNode imageNode = reqContentArray.addObject();
        imageNode.put("type", "input_image");
        imageNode.put("image_url", imageUrl);

        ObjectNode textNode = reqContentArray.addObject();
        textNode.put("type", "input_text");
        textNode.put("text", userText);

        RequestBody body = RequestBody.create(
                root.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // --- 2. 发送请求与解析部分 ---
        try (Response response = client.newCall(request).execute()) {
            String bodyString = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) throw new IOException("HTTP " + response.code() + ": " + bodyString);

            JsonNode rootNode = mapper.readTree(bodyString);
            JsonNode outputArray = rootNode.path("output");

            if (outputArray.isArray()) {
                for (JsonNode item : outputArray) {
                    if ("message".equals(item.path("type").asText())) {
                        JsonNode contentArray = item.path("content");
                        if (contentArray.isArray() && contentArray.has(0)) {
                            // 修正：进入第一个对象后，取名为 "text" 的字段值
                            return contentArray.get(0).path("text").asText();
                        }
                    }
                }
            }
            return "未能解析到有效内容";
        }
    }


}