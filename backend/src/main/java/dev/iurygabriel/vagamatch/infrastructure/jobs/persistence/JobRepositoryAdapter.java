package dev.iurygabriel.vagamatch.infrastructure.jobs.persistence;

import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Adapter de persistência do {@link JobRepository} usando JdbcClient,
 * com suporte nativo aos tipos pgvector (vector) e text[] do Postgres.
 */
@Repository
public class JobRepositoryAdapter implements JobRepository {

    private static final String UPSERT = """
            INSERT INTO jobs (id, source, external_id, title, company_name, description, url,
                              work_model, seniority, location, stack, embedding, posted_at, created_at)
            VALUES (:id, :source, :externalId, :title, :companyName, :description, :url,
                    :workModel, :seniority, :location, CAST(:stack AS text[]), CAST(:embedding AS vector),
                    :postedAt, :createdAt)
            ON CONFLICT (source, external_id) DO UPDATE SET
                title = EXCLUDED.title, company_name = EXCLUDED.company_name, description = EXCLUDED.description,
                url = EXCLUDED.url, work_model = EXCLUDED.work_model, seniority = EXCLUDED.seniority,
                location = EXCLUDED.location, stack = EXCLUDED.stack, embedding = EXCLUDED.embedding,
                posted_at = EXCLUDED.posted_at
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, source, external_id, title, company_name, description, url, work_model, seniority,
                   location, stack, embedding::text AS embedding_text, posted_at, created_at
            FROM jobs WHERE id = :id
            """;

    private final JdbcClient jdbc;

    public JobRepositoryAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Job save(Job job) {
        jdbc.sql(UPSERT)
                .param("id", job.id())
                .param("source", job.source())
                .param("externalId", job.externalId())
                .param("title", job.title())
                .param("companyName", job.companyName())
                .param("description", job.description())
                .param("url", job.url())
                .param("workModel", job.workModel().name())
                .param("seniority", job.seniority().name())
                .param("location", job.location(), Types.VARCHAR)
                .param("stack", toArrayLiteral(job.stack()), Types.VARCHAR)
                .param("embedding", toVectorLiteral(job.embedding()), Types.VARCHAR)
                .param("postedAt", toOffset(job.postedAt()), Types.TIMESTAMP_WITH_TIMEZONE)
                .param("createdAt", toOffset(job.createdAt()), Types.TIMESTAMP_WITH_TIMEZONE)
                .update();
        return job;
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return jdbc.sql(SELECT_BY_ID)
                .param("id", id)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public boolean existsBySourceAndExternalId(String source, String externalId) {
        return Boolean.TRUE.equals(jdbc.sql(
                        "SELECT EXISTS(SELECT 1 FROM jobs WHERE source = :source AND external_id = :externalId)")
                .param("source", source)
                .param("externalId", externalId)
                .query(Boolean.class)
                .single());
    }

    @Override
    public long count() {
        return jdbc.sql("SELECT count(*) FROM jobs").query(Long.class).single();
    }

    @Override
    public long countWithEmbedding() {
        return jdbc.sql("SELECT count(*) FROM jobs WHERE embedding IS NOT NULL").query(Long.class).single();
    }

    private Job mapRow(ResultSet rs, int rowNum) throws SQLException {
        Array stackArray = rs.getArray("stack");
        String[] stack = stackArray == null ? new String[0] : (String[]) stackArray.getArray();
        Timestamp posted = rs.getTimestamp("posted_at");
        return Job.rehydrate(
                rs.getObject("id", UUID.class),
                rs.getString("source"),
                rs.getString("external_id"),
                rs.getString("title"),
                rs.getString("company_name"),
                rs.getString("description"),
                rs.getString("url"),
                WorkModel.valueOf(rs.getString("work_model")),
                Seniority.valueOf(rs.getString("seniority")),
                rs.getString("location"),
                TechStack.fromArray(stack),
                parseVector(rs.getString("embedding_text")),
                posted == null ? null : posted.toInstant(),
                rs.getTimestamp("created_at").toInstant()
        );
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    private static String toVectorLiteral(Embedding embedding) {
        if (embedding == null) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float value : embedding.vector()) {
            joiner.add(Float.toString(value));
        }
        return joiner.toString();
    }

    private static Embedding parseVector(String text) {
        if (text == null || text.length() < 3) {
            return null;
        }
        String body = text.substring(1, text.length() - 1);
        if (body.isBlank()) {
            return null;
        }
        String[] parts = body.split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vector[i] = Float.parseFloat(parts[i].trim());
        }
        return new Embedding(vector);
    }

    private static String toArrayLiteral(TechStack stack) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for (String item : stack.items()) {
            joiner.add("\"" + item.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
        }
        return joiner.toString();
    }
}
