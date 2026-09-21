package dev.iurygabriel.vagamatch.jobs.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Value Object com as tecnologias da vaga, normalizadas (minúsculas, sem espaços/brancos).
 */
public record TechStack(Set<String> items) {

    public TechStack {
        items = (items == null ? Set.<String>of() : items).stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toUnmodifiableSet());
    }

    public static TechStack of(String... techs) {
        return new TechStack(Set.of(techs));
    }

    public boolean contains(String tech) {
        return tech != null && items.contains(tech.trim().toLowerCase());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public String asText() {
        return items.stream().sorted().collect(Collectors.joining(", "));
    }

    public String[] toArray() {
        return items.toArray(String[]::new);
    }

    public static TechStack fromArray(String[] techs) {
        return new TechStack(techs == null ? Set.of() : Arrays.stream(techs).collect(Collectors.toSet()));
    }
}
