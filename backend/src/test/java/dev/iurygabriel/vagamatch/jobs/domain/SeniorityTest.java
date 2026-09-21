package dev.iurygabriel.vagamatch.jobs.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Seniority.fromTitle (heurística por título)")
class SeniorityTest {

    @ParameterizedTest
    @CsvSource({
            "Senior Frontend Developer, SENIOR",
            "Desenvolvedor Backend Júnior, JUNIOR",
            "Tech Lead Angular, LEAD",
            "Estágio em Desenvolvimento, INTERN",
            "Desenvolvedor Pleno React, MID",
            "Software Engineer, MID"
    })
    void infere_senioridade(String title, Seniority expected) {
        assertThat(Seniority.fromTitle(title)).isEqualTo(expected);
    }
}
