# Swiss Travel Advisor — Hybrid Retrieval + Local AI Mode

Swiss Travel Advisor is an AI-powered travel assistant for discovering Swiss destinations, hotels, and activities using natural language queries.

This fork is based on the original Swiss Travel Advisor project by Alina Yurenko:
`https://github.com/alina-yur/swiss-travel-advisor`

The original project demonstrates a Micronaut + LangChain4j + Oracle AI Database + OpenAI + GraalVM Native Image based travel assistant.
This fork keeps the original idea intact, but adds a retrieval-quality upgrade, local AI mode, in-memory vector search, reranking, and retrieval metrics.

---

## What This Fork Adds

This fork adds the following upgrades:

* Hybrid retrieval reranking for destination search.
* Local AI mode using Ollama embeddings.
* In-memory vector search as a free local alternative to Oracle vector search.
* Lightweight reranking layer after vector retrieval.
* Retrieval timing metrics for observability.
* Unit test for the reranking service.
* Local search endpoint for testing semantic retrieval without Oracle Database or OpenAI credentials.

---

## Original Application

The original Swiss Travel Advisor allows users to ask questions such as:

```text
recommend a cozy ski town
add Zermatt to my wishlist
I want to visit a peaceful mountain resort
```

The assistant understands intent using embeddings and semantic search instead of relying only on exact keyword matching.

Original stack:

* Micronaut 5
* LangChain4j
* Oracle AI Database
* OpenAI embeddings
* GraalVM Native Image

![Micronaut LangChain4j Architecture](assets/micronaut-langchain4j-architecture.png)

---

## Original Flow

In the original implementation:

```text
User query
→ OpenAI embedding
→ Oracle AI Database vector search
→ top results
→ LangChain4j tool execution
→ LLM response
```

Oracle Database performs vector similarity search using `VECTOR_DISTANCE(..., COSINE)` to retrieve semantically relevant destinations, hotels, and activities.

---

## Architecture

Original components:

* `SwissTravelAssistant` — LangChain4j `@AiService` for conversation and tool orchestration.
* `TravelTools` — `@Tool` methods for semantic search and wishlist management.
* Repositories — JDBC-based repositories using Oracle vector search.
* `EmbeddingService` — generates embeddings using OpenAI.
* `DataInitializer` — generates and persists embeddings on startup.

Added in this fork:

* `RerankingService` — lightweight local reranking layer.
* `OllamaEmbeddingService` — generates local embeddings using Ollama.
* `LocalVectorSearchService` — performs in-memory cosine similarity search.
* `LocalDestinationDataProvider` — provides local Swiss destination data.
* `LocalSearchController` — exposes a local search endpoint for testing retrieval and reranking.
* `application-local.properties` — allows local mode to run without Oracle/OpenAI setup.

---

## Enhancement 1: Hybrid Retrieval Reranking

The original destination search returned the top 5 results directly from vector similarity search.

### Previous Flow

```text
User query
→ query embedding
→ Oracle vector search top 5
→ final results returned to the LLM
```

### Updated Flow

```text
User query
→ query embedding
→ Oracle vector search top 30 candidates
→ local reranking layer
→ final top 5 results returned to the LLM
```

The goal is to keep vector search as the fast high-recall retrieval layer, while adding a second precision-focused reranking step before the final context is passed to the LLM.

The current reranker is a lightweight local scoring service. It is not a real Cross Encoder yet. It validates the architecture without adding another external model dependency. In a future version, the same `RerankingService` can be replaced with a Cross Encoder or LLM-based reranker.

---

## Enhancement 2: Local AI Mode with Ollama

The original app requires:

```text
OpenAI API key
Oracle Database connection
```

This fork adds a local AI mode that can run without those paid/external dependencies.

### Local Mode Flow

```text
User query
→ Ollama embedding using nomic-embed-text
→ in-memory vector search over local Swiss destinations
→ top candidates
→ RerankingService
→ final ranked results
→ JSON response with metrics
```

Local mode uses:

* Ollama
* `nomic-embed-text` embedding model
* Java in-memory vector search
* Cosine similarity
* Local reranking
* Micronaut REST endpoint

This makes the retrieval and reranking pipeline easier to test, explain, and demonstrate.

---

## Local Mode Setup

### 1. Install Ollama

Install Ollama and make sure it is running locally.

Check:

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

Expected startup message:

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

The local endpoint returns timing metrics to make the retrieval pipeline observable.

| Metric                       | Meaning                                                    |
| ---------------------------- | ---------------------------------------------------------- |
| `candidateCount`             | Number of candidates retrieved before reranking            |
| `finalResultCount`           | Number of final results after reranking                    |
| `embeddingAndVectorSearchMs` | Time taken for query embedding and in-memory vector search |
| `rerankingMs`                | Time taken by the reranking layer                          |
| `totalMs`                    | Total retrieval pipeline time                              |

---

## Sample Local Mode Results

The local AI mode was tested using Ollama `nomic-embed-text` embeddings, in-memory cosine similarity search, and the local reranking layer.

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

This result shows that vector search retrieves semantically relevant mountain and nature destinations, while the reranker adjusts the final ordering based on stronger query-term relevance such as `peaceful`, `mountain`, `village`, `hiking`, `trails`, and `views`.

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

This result shows the benefit of reranking. Zurich had the highest vector similarity score, but Bern was promoted to the top after reranking because its description more directly matches the query terms: `historic`, `city`, `museums`, `old town`, `relaxed cafes`, and `cultural sightseeing`.

Note: timings are local development measurements and may vary depending on Ollama warm-up, model loading, and machine performance.

---

## Original Mode Retrieval Instrumentation

The original Oracle/OpenAI destination search flow has also been instrumented with basic timing metrics inside `TravelTools.java`.

When the original `/api/chat` flow calls `searchDestinations`, the tool now measures:

| Metric           | Meaning                                                  |
| ---------------- | -------------------------------------------------------- |
| `candidates`     | Number of vector-search candidates retrieved from Oracle |
| `finalResults`   | Number of final results after reranking                  |
| `vectorSearchMs` | Time spent in Oracle vector search                       |
| `rerankingMs`    | Time spent in the reranking layer                        |
| `totalMs`        | Total destination retrieval time                         |

The original mode flow is:

```text
User query
→ OpenAI embedding
→ Oracle vector search top 30 candidates
→ RerankingService
→ final top 5 results
→ LLM response
```

This makes the original Oracle/OpenAI path benchmark-ready, while the local Ollama mode provides a free way to test the same retrieval and reranking idea without external credentials.

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

## Files Added or Updated

### Added

* `src/main/java/com/example/local/LocalDestination.java`
* `src/main/java/com/example/local/LocalDestinationDataProvider.java`
* `src/main/java/com/example/local/OllamaEmbeddingService.java`
* `src/main/java/com/example/local/LocalVectorSearchService.java`
* `src/main/java/com/example/local/LocalSearchController.java`
* `src/main/resources/application-local.properties`
* `src/test/java/com/example/service/RerankingServiceTest.java`

### Updated

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

The test checks that the reranker can rank a more relevant Swiss mountain destination above less relevant city destinations for a travel-style query.

---

## Original Mode: Oracle + OpenAI

The original flow is still available.

Required environment variables:

```bash
export ORACLE_JDBC_URL='<oracle-jdbc-url>'
export DB_PASSWORD=
export OPENAI_API_KEY=your-key
```

Optional:

```bash
export DB_USERNAME=ADMIN
```

Run:

```bash
./mvnw mn:run
```

Example original API request:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "I want to visit a peaceful mountain resort"}'
```

---

## Building a Native Image

```bash
./mvnw package -Dpackaging=native-image
./target/swiss-travel-advisor
```

The original native executable:

* Has a size of around 132 MB.
* Starts and connects to the database in around 122 ms.
* Consumes around 98 MB RAM under load.

---

## Why This Upgrade Matters

This fork adds two important improvements.

### 1. Better Retrieval Quality

Instead of directly trusting the first vector search results, the app now supports a second reranking stage:

```text
Vector search = fast high-recall retrieval
Reranking = precision improvement
```

### 2. Easier Local Experimentation

The local Ollama mode makes the retrieval pipeline testable without paid services:

```text
No OpenAI key
No Oracle DB
Local embeddings
In-memory vector search
Reranking metrics
```

This is useful for understanding and experimenting with RAG retrieval architecture before connecting to production-grade services.

---

