package dev.iurygabriel.vagamatch.jobs.domain;

import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root do contexto de Vagas.
 * Domínio puro: sem anotações de JPA/Spring. A persistência é responsabilidade da infraestrutura.
 */
public class Job {

    private final UUID id;
    private final String source;
    private final String externalId;
    private final String title;
    private final String companyName;
    private final String description;
    private final String url;
    private final WorkModel workModel;
    private final Seniority seniority;
    private final String location;
    private final TechStack stack;
    private final Instant postedAt;
    private final Instant createdAt;

    private Embedding embedding; // atribuído após a ingestão, pelo provedor de IA

    private Job(UUID id, String source, String externalId, String title, String companyName,
                String description, String url, WorkModel workModel, Seniority seniority,
                String location, TechStack stack, Embedding embedding, Instant postedAt, Instant createdAt) {
        this.id = requireNonNull(id, "id");
        this.source = requireText(source, "source");
        this.externalId = requireText(externalId, "externalId");
        this.title = requireText(title, "title");
        this.companyName = requireText(companyName, "companyName");
        this.description = requireText(description, "description");
        this.url = requireText(url, "url");
        this.workModel = requireNonNull(workModel, "workModel");
        this.seniority = requireNonNull(seniority, "seniority");
        this.location = location;
        this.stack = stack == null ? new TechStack(java.util.Set.of()) : stack;
        this.embedding = embedding;
        this.postedAt = postedAt;
        this.createdAt = requireNonNull(createdAt, "createdAt");
    }

    /** Cria uma nova vaga (gera id e createdAt). Usado na ingestão. */
    public static Job create(String source, String externalId, String title, String companyName,
                             String description, String url, WorkModel workModel, Seniority seniority,
                             String location, TechStack stack, Instant postedAt) {
        return new Job(UUID.randomUUID(), source, externalId, title, companyName, description, url,
                workModel, seniority, location, stack, null, postedAt, Instant.now());
    }

    /** Reconstrói uma vaga a partir da persistência (mantém id, embedding e createdAt). */
    public static Job rehydrate(UUID id, String source, String externalId, String title, String companyName,
                                String description, String url, WorkModel workModel, Seniority seniority,
                                String location, TechStack stack, Embedding embedding, Instant postedAt, Instant createdAt) {
        return new Job(id, source, externalId, title, companyName, description, url, workModel, seniority,
                location, stack, embedding, postedAt, createdAt);
    }

    /** Texto usado para gerar o embedding e alimentar o matching. */
    public String matchableText() {
        return "%s na %s. Stack: %s. Modelo: %s. Nível: %s. %s"
                .formatted(title, companyName, stack.asText(), workModel, seniority, description);
    }

    public void assignEmbedding(Embedding embedding) {
        this.embedding = requireNonNull(embedding, "embedding");
    }

    public boolean hasEmbedding() {
        return embedding != null;
    }

    private static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new DomainException("Campo obrigatório: " + field);
        }
        return value;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Campo obrigatório: " + field);
        }
        return value;
    }

    public UUID id() { return id; }
    public String source() { return source; }
    public String externalId() { return externalId; }
    public String title() { return title; }
    public String companyName() { return companyName; }
    public String description() { return description; }
    public String url() { return url; }
    public WorkModel workModel() { return workModel; }
    public Seniority seniority() { return seniority; }
    public String location() { return location; }
    public TechStack stack() { return stack; }
    public Embedding embedding() { return embedding; }
    public Instant postedAt() { return postedAt; }
    public Instant createdAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Job job && Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
