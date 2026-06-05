# Swiss Travel Advisor — Hybrid Retrieval + Local AI Mode

Swiss Travel Advisor is an AI-powered travel assistant for discovering Swiss destinations using natural language queries.

This project is based on the original Swiss Travel Advisor repository by Alina Yurenko:
`https://github.com/alina-yur/swiss-travel-advisor`

The original project demonstrates a Micronaut + LangChain4j travel assistant using OpenAI embeddings, Oracle AI Database vector search, and GraalVM Native Image.
This fork extends that idea with hybrid retrieval reranking, a local Ollama-based AI mode, in-memory vector search, and retrieval timing metrics.

---

## What This Fork Adds

This fork focuses on improving and experimenting with the retrieval pipeline.

Main additions:

* Hybrid retrieval reranking after vector search.
* Local AI mode using Ollama embeddings.
* In-memory vector search without Oracle Database.
* Local semantic search endpoint.
* Retrieval timing metrics.
* Unit test for the reranking layer.
* Separate local profile that runs without OpenAI or Oracle credentials.

---

## Architecture

<img width="1672" height="941" alt="architecure" src="https://github.com/user-attachments/assets/c213dc0c-c5af-4737-aaf7-684b5a5293d2" />



This fork supports two retrieval paths:

### 1. Original Mode

```text
User query
→ OpenAI embedding
→ Oracle AI Database vector search
→ top 30 candidates
→ RerankingService
→ final top 5 results
→ LLM response
```

### 2. Local AI Mode

```text
User query
→ Ollama embedding using nomic-embed-text
→ in-memory vector search over local Swiss destinations
→ top candidates
→ RerankingService
→ final ranked results
→ JSON response with metrics
```

The local mode makes the retrieval pipeline testable without requiring an OpenAI API key or Oracle Database connection.

---

## Why This Upgrade Matters

Vector search is fast and useful for high-recall retrieval, but the first vector results are not always the most precise for the user’s exact intent.

This fork adds a second retrieval stage:

```text
Vector search = fast high-recall candidate retrieval
Reranking = precision-focused final ordering
```

Instead of directly returning the first vector-search results, the system can now retrieve a larger candidate set and rerank it before returning the final results.

---

## Hybrid Retrieval Reranking

### Previous Flow

```text
User query
→ query embedding
→ vector search top 5
→ final results
```

### Updated Flow

```text
User query
→ query embedding
→ vector search top 30 candidates
→ local reranking layer
→ final top 5 results
```

The current reranker is implemented as a lightweight local scoring layer. It is not a real Cross Encoder yet. The goal is to validate the architecture first without adding another external model dependency.

Future versions can replace the same `RerankingService` with:

* Cross Encoder reranker
* LLM-based reranker
* dedicated reranking API

---

## Local AI Mode

The original application depends on:

```text
OpenAI API key
Oracle Database connection
```

This fork adds a local mode that can run without those external dependencies.

Local mode uses:

* Ollama
* `nomic-embed-text`
* in-memory vector search
* cosine similarity
* local reranking
* Micronaut REST endpoint

---

## Local Mode Setup

### 1. Install Ollama

Check Ollama:

```powershell
ollama --version
```

### 2. Pull the Embedding Model

```powershell
ollama pull nomic-embed-text
```

Check installed models:

```powershell
ollama list
```

Expected model:

```text
nomic-embed-text:latest
```

### 3. Run the Application in Local Mode

PowerShell:

```powershell
$env:MICRONAUT_ENVIRONMENTS="local"
.\mvnw.bat mn:run
```

Expected startup:

```text
Established active environments: [local]
Server Running: http://localhost:8080
```

Example observed startup:

```text
Established active environments: [local]
Startup completed in 857ms. Server Running: http://localhost:8080
```

---

## Local Search API

Endpoint:

```text
POST /api/local/search
```

Example request:

```powershell
$body = @{
  query = "peaceful mountain village with scenic hiking trails and lake views"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/local/search" -Method Post -Body $body -ContentType "application/json"
$response | ConvertTo-Json -Depth 10
```

Example response structure:

```json
{
  "query": "peaceful mountain village with scenic hiking trails and lake views",
  "candidateCount": 10,
  "finalResultCount": 5,
  "embeddingAndVectorSearchMs": 84,
  "rerankingMs": 2,
  "totalMs": 86,
  "beforeReranking": [],
  "afterReranking": []
}
```

---

## Retrieval Metrics

The local search endpoint returns timing metrics for retrieval observability.

| Metric                       | Meaning                                                    |
| ---------------------------- | ---------------------------------------------------------- |
| `candidateCount`             | Number of candidates retrieved before reranking            |
| `finalResultCount`           | Number of final results after reranking                    |
| `embeddingAndVectorSearchMs` | Time taken for query embedding and in-memory vector search |
| `rerankingMs`                | Time taken by the reranking layer                          |
| `totalMs`                    | Total retrieval pipeline time                              |

---

## Sample Local Mode Results

The following results were generated using:

```text
Ollama nomic-embed-text
in-memory cosine similarity search
local RerankingService
40 Swiss destination records
```

---

### Query 1: Mountain and Nature Intent

```text
peaceful mountain village with scenic hiking trails and lake views
```

Metrics observed:

| Metric                    | Value |
| ------------------------- | ----: |
| Candidate count           |    10 |
| Final result count        |     5 |
| Embedding + vector search | 84 ms |
| Reranking                 |  2 ms |
| Total retrieval time      | 86 ms |

Top results before reranking:

| Rank | Destination   | Region           | Similarity score |
| ---: | ------------- | ---------------- | ---------------: |
|    1 | Grindelwald   | Bernese Oberland |           0.7772 |
|    2 | Murren        | Bernese Oberland |           0.7756 |
|    3 | Lauterbrunnen | Bernese Oberland |           0.7658 |
|    4 | Brienz        | Bern             |           0.7455 |
|    5 | Wengen        | Bernese Oberland |           0.7386 |

Top results after reranking:

| Rank | Destination   | Region           | Similarity score |
| ---: | ------------- | ---------------- | ---------------: |
|    1 | Grindelwald   | Bernese Oberland |           0.7772 |
|    2 | Lauterbrunnen | Bernese Oberland |           0.7658 |
|    3 | Wengen        | Bernese Oberland |           0.7386 |
|    4 | Murren        | Bernese Oberland |           0.7756 |
|    5 | Brienz        | Bern             |           0.7455 |

This shows that vector search retrieves semantically relevant mountain destinations, while reranking adjusts the final ordering based on stronger query-term relevance such as `peaceful`, `mountain`, `village`, `hiking`, `trails`, and `views`.

---

### Query 2: Historic City and Culture Intent

```text
historic Swiss city with museums old town culture and relaxed cafes
```

Metrics observed:

| Metric                    |   Value |
| ------------------------- | ------: |
| Candidate count           |      10 |
| Final result count        |       5 |
| Embedding + vector search | 2433 ms |
| Reranking                 |    2 ms |
| Total retrieval time      | 2435 ms |

Top results before reranking:

| Rank | Destination | Region              | Similarity score |
| ---: | ----------- | ------------------- | ---------------: |
|    1 | Zurich      | Zurich              |           0.8186 |
|    2 | Chur        | Graubunden          |           0.7945 |
|    3 | Bern        | Bern                |           0.7915 |
|    4 | Lucerne     | Central Switzerland |           0.7838 |
|    5 | Basel       | Basel-Stadt         |           0.7655 |

Top results after reranking:

| Rank | Destination | Region              | Similarity score |
| ---: | ----------- | ------------------- | ---------------: |
|    1 | Bern        | Bern                |           0.7915 |
|    2 | Chur        | Graubunden          |           0.7945 |
|    3 | Lucerne     | Central Switzerland |           0.7838 |
|    4 | St. Gallen  | St. Gallen          |           0.7612 |
|    5 | Zurich      | Zurich              |           0.8186 |

This shows the benefit of reranking. Zurich had the highest vector similarity score, but Bern was promoted to the top because its description more directly matches the query terms: `historic`, `city`, `museums`, `old town`, `relaxed cafes`, and `cultural sightseeing`.

Note: timings are local development measurements and may vary depending on Ollama warm-up, model loading, and machine performance.

---

## Original Mode Instrumentation

The original `/api/chat` destination search path has also been instrumented with timing metrics inside `TravelTools.java`.

When the original flow calls `searchDestinations`, it now measures:

| Metric           | Meaning                                      |
| ---------------- | -------------------------------------------- |
| `candidates`     | Number of vector-search candidates retrieved |
| `finalResults`   | Number of final results after reranking      |
| `vectorSearchMs` | Time spent in vector search                  |
| `rerankingMs`    | Time spent in the reranking layer            |
| `totalMs`        | Total destination retrieval time             |

This makes the original retrieval path benchmark-ready, while local mode provides a free way to test the same retrieval and reranking idea.

---

## Example Queries for Local Mode

```text
peaceful mountain village with scenic hiking trails and lake views
```

```text
luxury alpine resort with skiing spa hotels and mountain views
```

```text
adventure destination for paragliding hiking and outdoor sports
```

```text
historic Swiss city with museums old town culture and relaxed cafes
```

```text
relaxed lakeside town with beautiful walks mountain views and mild weather
```

---

## Files Added

* `src/main/java/com/example/local/LocalDestination.java`
* `src/main/java/com/example/local/LocalDestinationDataProvider.java`
* `src/main/java/com/example/local/OllamaEmbeddingService.java`
* `src/main/java/com/example/local/LocalVectorSearchService.java`
* `src/main/java/com/example/local/LocalSearchController.java`
* `src/main/resources/application-local.properties`
* `src/test/java/com/example/service/RerankingServiceTest.java`

---

## Files Updated

* `src/main/java/com/example/tools/TravelTools.java`
* `src/main/java/com/example/service/RerankingService.java`
* `src/main/java/com/example/service/DataInitializer.java`
* `src/main/java/com/example/service/EmbeddingService.java`
* `src/main/java/com/example/service/SwissTravelAssistant.java`
* `src/main/java/com/example/controller/ChatController.java`
* `src/main/java/com/example/controller/WishlistController.java`
* `src/main/java/com/example/repository/ActivityRepository.java`
* `src/main/java/com/example/repository/DestinationRepository.java`
* `src/main/java/com/example/repository/HotelRepository.java`
* `src/main/java/com/example/repository/WishlistRepository.java`
* `pom.xml`
* `README.md`

---

## Unit Test

The reranking layer is tested independently without Oracle Database or OpenAI.

Run:

```powershell
.\mvnw.bat test
```

The test verifies that the reranker can rank a more relevant Swiss mountain destination above less relevant city destinations for a travel-style query.

---

## Build

```powershell
.\mvnw.bat clean package
```

---

## Future Work

Possible next improvements:

* Add a dashboard to visualize:

  * query
  * before-reranking results
  * after-reranking results
  * timing metrics
* Replace the lightweight reranker with a Cross Encoder.
* Add LLM-based reranking.
* Add local hotel and activity search.
* Add optional Ollama chat response generation.
* Compare baseline vector-only retrieval vs reranked retrieval.
* Benchmark JVM vs GraalVM Native Image startup and memory usage in local mode.
