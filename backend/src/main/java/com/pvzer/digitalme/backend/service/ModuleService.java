package com.pvzer.digitalme.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ModuleService {

    private final WebClient webClient;

    public ModuleService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://ark.cn-beijing.volces.com").build();
    }

    public void callArkApi(String apiKey, String model, String text) {
        // 构建请求体 (你可以定义专门的 POJO 类来替代 Map)
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", text)
                )
        );

        this.webClient.post()
                .uri("/api/v3/responses")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class) // 将响应转为字符串
                .subscribe(response -> {
                    System.out.println("模型回复: " + response);
                }, error -> {
                    System.err.println("请求失败: " + error.getMessage());
                });
    }
}