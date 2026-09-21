package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.candidates.application.MatchResumeUseCase;
import dev.iurygabriel.vagamatch.interfaces.rest.dto.JobMatchResponse;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/** Endpoint principal: recebe o currículo em PDF e devolve as vagas mais compatíveis. */
@RestController
public class MatchController {

    private final MatchResumeUseCase matchResume;

    public MatchController(MatchResumeUseCase matchResume) {
        this.matchResume = matchResume;
    }

    @PostMapping(path = "/api/match", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<JobMatchResponse> match(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        byte[] bytes;
        try {
            bytes = resume.getBytes();
        } catch (IOException e) {
            throw new DomainException("Falha ao ler o arquivo enviado");
        }
        return matchResume.match(bytes, MatchCriteria.topN(limit)).stream()
                .map(JobMatchResponse::from)
                .toList();
    }
}
