package dev.iurygabriel.vagamatch.jobs.application.port;

import dev.iurygabriel.vagamatch.jobs.domain.Job;

import java.util.List;

/**
 * Porta de entrada de dados: uma fonte de vagas (Remotive, Greenhouse, ...).
 * O adapter na infraestrutura sabe chamar a API e mapear para o domínio.
 */
public interface JobSource {

    /** Identificador da fonte (ex: "remotive"). */
    String name();

    /** Busca as vagas atuais da fonte, já mapeadas para o domínio (sem embedding). */
    List<Job> fetch();
}
