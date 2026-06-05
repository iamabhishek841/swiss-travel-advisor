package com.example.local;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.util.List;

@Requires(env = "local")
@Singleton
public class LocalDestinationDataProvider {

    public List<LocalDestination> findAll() {
        return List.of(
                new LocalDestination(
                        1L,
                        "Zermatt",
                        "Valais",
                        "A peaceful mountain resort with luxury hotels, Matterhorn views, romantic scenery, skiing, spa stays, and alpine hiking."
                ),
                new LocalDestination(
                        2L,
                        "Interlaken",
                        "Bernese Oberland",
                        "A scenic adventure destination between two lakes, known for paragliding, hiking, mountains, boat trips, and outdoor sports."
                ),
                new LocalDestination(
                        3L,
                        "Zurich",
                        "Zurich",
                        "A vibrant Swiss city with museums, nightlife, shopping, business districts, restaurants, cultural attractions, and lake views."
                ),
                new LocalDestination(
                        4L,
                        "Lucerne",
                        "Central Switzerland",
                        "A relaxed lakeside city with a historic old town, wooden bridge, mountain views, lake cruises, and peaceful walks."
                ),
                new LocalDestination(
                        5L,
                        "St. Moritz",
                        "Graubunden",
                        "A luxury alpine resort famous for skiing, winter sports, honeymoon stays, spas, premium hotels, and mountain scenery."
                ),
                new LocalDestination(
                        6L,
                        "Grindelwald",
                        "Bernese Oberland",
                        "A calm mountain village surrounded by dramatic peaks, hiking trails, scenic train rides, cozy stays, and peaceful alpine views."
                ),
                new LocalDestination(
                        7L,
                        "Geneva",
                        "Geneva",
                        "A lakeside international city known for diplomacy, business travel, international organizations, culture, parks, and restaurants."
                ),
                new LocalDestination(
                        8L,
                        "Davos",
                        "Graubunden",
                        "A high-altitude town known for skiing, conferences, winter sports, mountain air, hiking, and outdoor activities."
                ),
                new LocalDestination(
                        9L,
                        "Lugano",
                        "Ticino",
                        "A sunny lakeside destination with Italian-speaking culture, mild weather, palm trees, lake views, and relaxed holidays."
                ),
                new LocalDestination(
                        10L,
                        "Lausanne",
                        "Vaud",
                        "A cultural lakeside city with the Olympic Museum, vineyards nearby, old town streets, student life, and Lake Geneva scenery."
                ),
                new LocalDestination(
                        11L,
                        "Montreux",
                        "Vaud",
                        "A romantic lakeside town on Lake Geneva known for the jazz festival, promenade walks, mild climate, castles, and mountain views."
                ),
                new LocalDestination(
                        12L,
                        "Bern",
                        "Bern",
                        "A historic capital city with medieval streets, museums, river views, relaxed cafes, old town architecture, and cultural sightseeing."
                ),
                new LocalDestination(
                        13L,
                        "Basel",
                        "Basel-Stadt",
                        "A cultural city on the Rhine known for art museums, architecture, galleries, river walks, and cross-border travel."
                ),
                new LocalDestination(
                        14L,
                        "Lauterbrunnen",
                        "Bernese Oberland",
                        "A peaceful valley village famous for waterfalls, cliffs, hiking, scenic mountain views, and quiet nature stays."
                ),
                new LocalDestination(
                        15L,
                        "Wengen",
                        "Bernese Oberland",
                        "A car-free alpine village with calm mountain scenery, ski access, hiking trails, family-friendly stays, and peaceful views."
                ),
                new LocalDestination(
                        16L,
                        "Murren",
                        "Bernese Oberland",
                        "A quiet car-free mountain village with dramatic cliff views, hiking, skiing, romantic chalets, and peaceful alpine atmosphere."
                ),
                new LocalDestination(
                        17L,
                        "Gstaad",
                        "Bernese Oberland",
                        "A luxury mountain resort with boutique hotels, skiing, spas, fine dining, romantic stays, and premium alpine experiences."
                ),
                new LocalDestination(
                        18L,
                        "Verbier",
                        "Valais",
                        "A lively ski resort known for advanced slopes, nightlife, mountain sports, luxury chalets, and winter adventure."
                ),
                new LocalDestination(
                        19L,
                        "Saas-Fee",
                        "Valais",
                        "A car-free glacier village with skiing, snow activities, hiking, mountain views, and relaxed alpine stays."
                ),
                new LocalDestination(
                        20L,
                        "Andermatt",
                        "Uri",
                        "A growing alpine resort with skiing, mountain passes, luxury hotels, scenic train routes, and quiet winter escapes."
                ),
                new LocalDestination(
                        21L,
                        "Ascona",
                        "Ticino",
                        "A charming lakeside town with Mediterranean style, cafes, art galleries, warm weather, romantic walks, and relaxed holidays."
                ),
                new LocalDestination(
                        22L,
                        "Locarno",
                        "Ticino",
                        "A sunny lakeside destination known for film festivals, palm trees, lake views, old town streets, and mild climate."
                ),
                new LocalDestination(
                        23L,
                        "Neuchatel",
                        "Neuchatel",
                        "A calm lakeside city with old town charm, vineyards, watchmaking history, cultural sites, and peaceful lake walks."
                ),
                new LocalDestination(
                        24L,
                        "Fribourg",
                        "Fribourg",
                        "A medieval university town with bridges, old streets, cultural heritage, cafes, and scenic views over the Sarine river."
                ),
                new LocalDestination(
                        25L,
                        "Thun",
                        "Bern",
                        "A pretty lakeside town with a castle, mountain views, boat trips, relaxed old town, and access to the Bernese Oberland."
                ),
                new LocalDestination(
                        26L,
                        "Brienz",
                        "Bern",
                        "A quiet lakeside village known for turquoise water, wood carving, mountain railway, peaceful scenery, and nature stays."
                ),
                new LocalDestination(
                        27L,
                        "Appenzell",
                        "Appenzell Innerrhoden",
                        "A traditional village with colorful houses, local culture, hiking, cheese, countryside landscapes, and peaceful rural charm."
                ),
                new LocalDestination(
                        28L,
                        "St. Gallen",
                        "St. Gallen",
                        "A historic city known for its abbey library, old town, textile heritage, student life, and cultural sightseeing."
                ),
                new LocalDestination(
                        29L,
                        "Schaffhausen",
                        "Schaffhausen",
                        "A historic town near the Rhine Falls with old streets, river views, fortress walks, and easy nature excursions."
                ),
                new LocalDestination(
                        30L,
                        "Rapperswil",
                        "St. Gallen",
                        "A lakeside town near Zurich with a castle, rose gardens, lake promenade, family-friendly walks, and relaxed day trips."
                ),
                new LocalDestination(
                        31L,
                        "Arosa",
                        "Graubunden",
                        "A peaceful mountain resort with skiing, lakes, hiking, wellness hotels, family stays, and quiet alpine scenery."
                ),
                new LocalDestination(
                        32L,
                        "Lenzerheide",
                        "Graubunden",
                        "A mountain resort with skiing, biking, lake activities, family holidays, hiking trails, and relaxed outdoor stays."
                ),
                new LocalDestination(
                        33L,
                        "Engelberg",
                        "Obwalden",
                        "A mountain town known for Mount Titlis, skiing, cable cars, monastery, hiking, and snow activities."
                ),
                new LocalDestination(
                        34L,
                        "Crans-Montana",
                        "Valais",
                        "A sunny alpine resort with golf, skiing, luxury hotels, spas, mountain views, and relaxed premium stays."
                ),
                new LocalDestination(
                        35L,
                        "Sion",
                        "Valais",
                        "A historic town in the Rhone valley with castles, vineyards, sunny weather, mountain access, and cultural walks."
                ),
                new LocalDestination(
                        36L,
                        "Vevey",
                        "Vaud",
                        "A relaxed Lake Geneva town known for lakeside walks, food culture, Chaplin history, vineyards, and mountain views."
                ),
                new LocalDestination(
                        37L,
                        "Gruyeres",
                        "Fribourg",
                        "A medieval village famous for cheese, castle views, traditional Swiss food, countryside scenery, and romantic day trips."
                ),
                new LocalDestination(
                        38L,
                        "Chur",
                        "Graubunden",
                        "Switzerland's oldest city with old town streets, mountain train access, culture, hiking gateways, and relaxed sightseeing."
                ),
                new LocalDestination(
                        39L,
                        "Flims",
                        "Graubunden",
                        "A nature-focused alpine destination with lakes, forests, hiking, skiing, wellness stays, and peaceful mountain scenery."
                ),
                new LocalDestination(
                        40L,
                        "Baden",
                        "Aargau",
                        "A spa town near Zurich known for thermal baths, wellness hotels, old town streets, river walks, and relaxed short breaks."
                )
        );
    }
}