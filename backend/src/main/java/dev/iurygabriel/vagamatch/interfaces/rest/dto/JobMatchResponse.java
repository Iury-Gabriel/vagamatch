package dev.iurygabriel.vagamatch.interfaces.rest.dto;

import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;

import java.util.List;

/** Representação de uma vaga compatível para a API. */
public record JobMatchResponse(
        String title,
        String company,
        String url,
        String workModel,
        String seniority,
        String location,
        List<String> stack,
        int scorePercent
) {
    public static JobMatchResponse from(JobMatch match) {
        Job job = match.job();
        return new JobMatchResponse(
                job.title(),
                job.companyName(),
                job.url(),
                job.workModel().name(),
                job.seniority().name(),
                job.location(),
                job.stack().items().stream().sorted().toList(),
                match.scorePercent());
    }
}
