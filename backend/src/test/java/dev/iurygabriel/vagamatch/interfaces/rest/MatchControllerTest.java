package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.candidates.application.MatchResumeUseCase;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MatchController (camada web)")
class MatchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MatchResumeUseCase matchResume;

    @Test
    @DisplayName("POST /api/match devolve as vagas compatíveis")
    void retorna_vagas_compativeis() throws Exception {
        Job job = Job.create("remotive", "1", "React Developer", "Acme", "desc", "https://x/1",
                WorkModel.REMOTE, Seniority.MID, "Brasil", TechStack.of("react", "node"), Instant.now());
        when(matchResume.match(any(), any())).thenReturn(List.of(new JobMatch(job, 0.91)));

        MockMultipartFile file = new MockMultipartFile(
                "resume", "cv.pdf", "application/pdf", "conteudo-pdf".getBytes());

        mvc.perform(multipart("/api/match").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("React Developer"))
                .andExpect(jsonPath("$[0].company").value("Acme"))
                .andExpect(jsonPath("$[0].scorePercent").value(91));
    }
}
