package com.example.local;

public record LocalDestination(
        Long id,
        String name,
        String region,
        String description
) {
    public String searchableText() {
        return name + " " + region + " " + description;
    }
}