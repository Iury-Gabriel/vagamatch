package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.jobs.application.IngestJobsUseCase;
import dev.iurygabriel.vagamatch.jobs.application.IngestJobsUseCase.IngestionResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Dispara a ingestão de vagas das fontes configuradas. (Será protegido por auth no futuro.) */
@RestController
public class IngestController {

    private final IngestJobsUseCase ingestJobs;

    public IngestController(IngestJobsUseCase ingestJobs) {
        this.ingestJobs = ingestJobs;
    }

    @PostMapping("/api/admin/ingest")
    public IngestionResult ingest() {
        return ingestJobs.run();
    }
}
