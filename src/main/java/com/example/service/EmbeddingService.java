package com.example.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.inject.Singleton;
import io.micronaut.context.annotation.Requires;

@Requires(notEnv = "local")
@Singleton
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] generateEmbedding(String text) {
        Embedding embedding = embeddingModel.embed(text).content();
        return embedding.vector();
    }
}
