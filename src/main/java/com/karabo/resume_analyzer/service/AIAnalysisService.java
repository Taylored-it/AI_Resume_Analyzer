package com.karabo.resume_analyzer.service;

import com.karabo.resume_analyzer.exception.AIAnalysisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIAnalysisService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();

    public String analyzeResume(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            throw new AIAnalysisException("No text could be extracted from the resume.");
        }

        log.info("Sending resume text to OpenAI ({} chars) using model '{}'", resumeText.length(), model);

        String prompt = """
                Analyze this resume. Identify skills and suggest improvements.

                Resume:
                """ + resumeText;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are a professional resume reviewer and career coach. "
                                + "Respond ONLY with a JSON object in this exact format: "
                                + "{\"summary\": \"...\", \"skills\": [\"skill1\", \"skill2\"], "
                                + "\"suggestions\": [\"suggestion1\", \"suggestion2\"]}"),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 1000,
                "response_format", Map.of("type", "json_object")
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            String result = (String) message.get("content");

            log.info("AI analysis completed successfully");
            return result;

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("OpenAI authentication failed — check your API key");
            throw new AIAnalysisException("Invalid OpenAI API key. Please check your configuration.", e);
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("OpenAI rate limit exceeded");
            throw new AIAnalysisException("AI service rate limit reached. Please wait a moment and try again.", e);
        } catch (HttpClientErrorException e) {
            log.error("OpenAI client error {}: {}", e.getStatusCode(), e.getMessage());
            throw new AIAnalysisException("AI service rejected the request: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            log.error("OpenAI server error {}: {}", e.getStatusCode(), e.getMessage());
            throw new AIAnalysisException("AI service is temporarily unavailable. Please try again later.", e);
        } catch (Exception e) {
            log.error("Unexpected error calling OpenAI", e);
            throw new AIAnalysisException("Failed to connect to AI service. Please try again.", e);
        }
    }
}
