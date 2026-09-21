package dev.iurygabriel.vagamatch.admin.application;

import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import org.springframework.stereotype.Service;

/** Caso de uso: números do painel administrativo. */
@Service
public class GetAdminStatsUseCase {

    private final JobRepository jobs;
    private final UserRepository users;

    public GetAdminStatsUseCase(JobRepository jobs, UserRepository users) {
        this.jobs = jobs;
        this.users = users;
    }

    public AdminStats execute() {
        return new AdminStats(jobs.count(), jobs.countWithEmbedding(), users.count());
    }

    public record AdminStats(long totalJobs, long jobsWithEmbedding, long totalUsers) {
    }
}
