package dev.iurygabriel.vagamatch.infrastructure.matching;

import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.matching.application.port.JobMatcher;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.StringJoiner;

/**
 * Adapter de matching sobre pgvector: ordena as vagas pela distância de cosseno
 * ao embedding do perfil e aplica filtros de negócio opcionais.
 */
@Repository
public class PgVectorJobMatcher implements JobMatcher {

    private final JdbcClient jdbc;

    public PgVectorJobMatcher(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<JobMatch> findMatches(Embedding profile, MatchCriteria criteria) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, source, external_id, title, company_name, description, url, work_model,
                       seniority, location, stack, posted_at, created_at,
                       1 - (embedding <=> CAST(:profile AS vector)) AS score
                FROM jobs
                WHERE embedding IS NOT NULL
                """);
        if (criteria.hasWorkModel()) {
            sql.append(" AND work_model = :workModel");
        }
        if (criteria.hasSeniorities()) {
            sql.append(" AND seniority IN (:seniorities)");
        }
        sql.append(" ORDER BY embedding <=> CAST(:profile AS vector) LIMIT :limit");

        var spec = jdbc.sql(sql.toString())
                .param("profile", toVectorLiteral(profile), Types.VARCHAR)
                .param("limit", criteria.limit());
        if (criteria.hasWorkModel()) {
            spec = spec.param("workModel", criteria.workModel().name());
        }
        if (criteria.hasSeniorities()) {
            spec = spec.param("seniorities", criteria.seniorities().stream().map(Enum::name).toList());
        }
        return spec.query(this::mapRow).list();
    }

    private JobMatch mapRow(ResultSet rs, int rowNum) throws SQLException {
        Array stackArray = rs.getArray("stack");
        String[] stack = stackArray == null ? new String[0] : (String[]) stackArray.getArray();
        Timestamp posted = rs.getTimestamp("posted_at");
        Job job = Job.rehydrate(
                rs.getObject("id", java.util.UUID.class),
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
                null,
                posted == null ? null : posted.toInstant(),
                rs.getTimestamp("created_at").toInstant());
        return new JobMatch(job, rs.getDouble("score"));
    }

    private static String toVectorLiteral(Embedding embedding) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float value : embedding.vector()) {
            joiner.add(Float.toString(value));
        }
        return joiner.toString();
    }
}
