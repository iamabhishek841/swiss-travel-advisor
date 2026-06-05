package com.example.local;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;

@Requires(env = "local")
@Singleton
public class OllamaEmbeddingService {

    private static final String OLLAMA_EMBEDDING_URL = "http://localhost:11434/api/embeddings";
    private static final String EMBEDDING_MODEL = "nomic-embed-text";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OllamaEmbeddingService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public float[] embed(String text) {
        try {
            String requestBody = objectMapper.writeValueAsString(
                    new EmbeddingRequest(EMBEDDING_MODEL, text)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_EMBEDDING_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Ollama embedding request failed: " + response.body());
            }

            EmbeddingResponse embeddingResponse = objectMapper.readValue(
                    response.body(),
                    EmbeddingResponse.class
            );

            return toFloatArray(embeddingResponse.embedding());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding using Ollama", e);
        }
    }

    private float[] toFloatArray(List<Double> values) {
        float[] result = new float[values.size()];

        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }

        return result;
    }

    @Serdeable
    public record EmbeddingRequest(
            String model,
            String prompt
    ) {
    }

    @Serdeable
    public record EmbeddingResponse(
            List<Double> embedding
    ) {
    }
}