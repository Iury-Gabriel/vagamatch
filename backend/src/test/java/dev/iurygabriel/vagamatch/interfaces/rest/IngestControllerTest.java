package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.jobs.application.IngestJobsUseCase;
import dev.iurygabriel.vagamatch.jobs.application.IngestJobsUseCase.IngestionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngestController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("IngestController (camada web)")
class IngestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private IngestJobsUseCase ingestJobs;

    @Test
    @DisplayName("POST /api/admin/ingest devolve o resultado da ingestão")
    void dispara_ingestao() throws Exception {
        when(ingestJobs.run()).thenReturn(new IngestionResult(7, 3));

        mvc.perform(post("/api/admin/ingest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(7))
                .andExpect(jsonPath("$.skipped").value(3));
    }
}
