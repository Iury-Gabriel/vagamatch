package dev.iurygabriel.vagamatch.infrastructure.jobs.source;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.iurygabriel.vagamatch.jobs.application.port.JobSource;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Fonte de vagas Remotive (https://remotive.com/api/remote-jobs) — todas remotas.
 * Adapter: chama a API pública e mapeia a resposta para o domínio {@link Job}.
 */
@Component
public class RemotiveJobSource implements JobSource {

    private static final String SOURCE = "remotive";
    private final RestClient client;
    private final int limit;

    public RemotiveJobSource() {
        this.client = RestClient.builder().baseUrl("https://remotive.com/api").build();
        this.limit = 50;
    }

    @Override
    public String name() {
        return SOURCE;
    }

    @Override
    public List<Job> fetch() {
        RemotiveResponse response = client.get()
                .uri(uri -> uri.path("/remote-jobs")
                        .queryParam("category", "software-dev")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(RemotiveResponse.class);

        if (response == null || response.jobs() == null) {
            return List.of();
        }
        return response.jobs().stream().map(this::toJob).toList();
    }

    private Job toJob(RemotiveJob raw) {
        String[] tags = raw.tags() == null ? new String[0] : raw.tags().toArray(String[]::new);
        return Job.create(
                SOURCE,
                String.valueOf(raw.id()),
                raw.title(),
                raw.companyName(),
                stripHtml(raw.description()),
                raw.url(),
                WorkModel.REMOTE,
                Seniority.fromTitle(raw.title()),
                raw.candidateRequiredLocation(),
                TechStack.fromArray(tags),
                parseDate(raw.publicationDate()));
    }

    private static String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        String text = html.replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ").replace("&amp;", "&")
                .replace("&lt;", "<").replace("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
        return text.length() > 5000 ? text.substring(0, 5000) : text;
    }

    private static Instant parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
        } catch (RuntimeException ignored) {
            try {
                return OffsetDateTime.parse(value).toInstant();
            } catch (RuntimeException ignored2) {
                return null;
            }
        }
    }

    private record RemotiveResponse(List<RemotiveJob> jobs) {
    }

    private record RemotiveJob(
            long id,
            String url,
            String title,
            @JsonProperty("company_name") String companyName,
            @JsonProperty("candidate_required_location") String candidateRequiredLocation,
            @JsonProperty("publication_date") String publicationDate,
            List<String> tags,
            String description
    ) {
    }
}
