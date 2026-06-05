package com.example.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RerankingServiceTest {

    private final RerankingService rerankingService = new RerankingService();

    @Test
    void shouldRankMostRelevantDestinationFirst() {
        List<TestDestination> candidates = List.of(
                new TestDestination("Zurich", "Zurich", "A vibrant city with museums, nightlife, shopping, and business districts."),
                new TestDestination("Zermatt", "Valais", "A peaceful mountain resort with luxury hotels, Matterhorn views, and romantic scenery."),
                new TestDestination("Geneva", "Geneva", "A lakeside city known for international organizations, business travel, and culture.")
        );

        List<TestDestination> results = rerankingService.rerank(
                "peaceful mountain resort for honeymoon",
                candidates,
                destination -> destination.name() + " " + destination.region() + " " + destination.description(),
                2
        );

        assertEquals(2, results.size());
        assertEquals("Zermatt", results.get(0).name());
    }

    private record TestDestination(String name, String region, String description) {
    }
}