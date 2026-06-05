package com.example.local;

import com.example.service.RerankingService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import io.micronaut.context.annotation.Requires;

@Requires(env = "local")
@Controller("/api/local")
public class LocalSearchController {

    private static final int CANDIDATE_LIMIT = 10;
    private static final int FINAL_RESULT_LIMIT = 5;

    private final LocalVectorSearchService vectorSearchService;
    private final RerankingService rerankingService;

    public LocalSearchController(
            LocalVectorSearchService vectorSearchService,
            RerankingService rerankingService
    ) {
        this.vectorSearchService = vectorSearchService;
        this.rerankingService = rerankingService;
    }

    @Post("/search")
    public LocalSearchResponse search(@Body LocalSearchRequest request) {
        long startTime = System.currentTimeMillis();

        List<LocalVectorSearchService.LocalSearchResult> candidates =
                vectorSearchService.search(request.query(), CANDIDATE_LIMIT);

        long vectorSearchEnd = System.currentTimeMillis();

        List<LocalVectorSearchService.LocalSearchResult> rerankedResults =
                rerankingService.rerank(
                        request.query(),
                        candidates,
                        result -> result.destination().searchableText(),
                        FINAL_RESULT_LIMIT
                );

        long rerankingEnd = System.currentTimeMillis();

        return new LocalSearchResponse(
                request.query(),
                candidates.size(),
                rerankedResults.size(),
                vectorSearchEnd - startTime,
                rerankingEnd - vectorSearchEnd,
                rerankingEnd - startTime,
                toResultItems(candidates),
                toResultItems(rerankedResults)
        );
    }

    private List<ResultItem> toResultItems(List<LocalVectorSearchService.LocalSearchResult> results) {
        return results.stream()
                .map(result -> new ResultItem(
                        result.destination().id(),
                        result.destination().name(),
                        result.destination().region(),
                        result.destination().description(),
                        result.similarityScore()
                ))
                .toList();
    }

    @Serdeable
    public record LocalSearchRequest(
            String query
    ) {
    }

    @Serdeable
    public record LocalSearchResponse(
            String query,
            int candidateCount,
            int finalResultCount,
            long embeddingAndVectorSearchMs,
            long rerankingMs,
            long totalMs,
            List<ResultItem> beforeReranking,
            List<ResultItem> afterReranking
    ) {
    }

    @Serdeable
    public record ResultItem(
            Long id,
            String name,
            String region,
            String description,
            double similarityScore
    ) {
    }
}