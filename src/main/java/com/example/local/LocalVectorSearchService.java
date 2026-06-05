package com.example.local;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import io.micronaut.context.annotation.Requires;

@Requires(env = "local")
@Singleton
public class LocalVectorSearchService {

    private static final int DEFAULT_CANDIDATE_LIMIT = 30;

    private final LocalDestinationDataProvider dataProvider;
    private final OllamaEmbeddingService embeddingService;

    private List<EmbeddedDestination> embeddedDestinations;

    public LocalVectorSearchService(
            LocalDestinationDataProvider dataProvider,
            OllamaEmbeddingService embeddingService
    ) {
        this.dataProvider = dataProvider;
        this.embeddingService = embeddingService;
    }

    @PostConstruct
    void initialize() {
        this.embeddedDestinations = dataProvider.findAll()
                .stream()
                .map(destination -> new EmbeddedDestination(
                        destination,
                        embeddingService.embed(destination.searchableText())
                ))
                .collect(Collectors.toList());
    }

    public List<LocalSearchResult> search(String query) {
        return search(query, DEFAULT_CANDIDATE_LIMIT);
    }

    public List<LocalSearchResult> search(String query, int limit) {
        float[] queryEmbedding = embeddingService.embed(query);

        return embeddedDestinations.stream()
                .map(embeddedDestination -> new LocalSearchResult(
                        embeddedDestination.destination(),
                        cosineSimilarity(queryEmbedding, embeddedDestination.embedding())
                ))
                .sorted(Comparator.comparingDouble(LocalSearchResult::similarityScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Embedding dimensions do not match.");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private record EmbeddedDestination(
            LocalDestination destination,
            float[] embedding
    ) {
    }

    public record LocalSearchResult(
            LocalDestination destination,
            double similarityScore
    ) {
    }
}