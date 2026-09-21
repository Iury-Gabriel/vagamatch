-- Extensão de vetores para busca por similaridade (matching por embedding)
CREATE EXTENSION IF NOT EXISTS vector;

-- Catálogo de vagas ingeridas das fontes (Greenhouse, Remotive, etc.)
CREATE TABLE jobs (
    id                UUID PRIMARY KEY,
    source            VARCHAR(40)  NOT NULL,
    external_id       VARCHAR(200) NOT NULL,
    title             VARCHAR(300) NOT NULL,
    company_name      VARCHAR(200) NOT NULL,
    description       TEXT         NOT NULL,
    url               VARCHAR(600) NOT NULL,
    work_model        VARCHAR(20)  NOT NULL,
    seniority         VARCHAR(20)  NOT NULL,
    location          VARCHAR(200),
    stack             TEXT[]       NOT NULL DEFAULT '{}',
    embedding         vector(1536),
    posted_at         TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_jobs_source_external UNIQUE (source, external_id)
);

-- Índice ANN (cosine) para acelerar o matching por similaridade
CREATE INDEX idx_jobs_embedding ON jobs USING hnsw (embedding vector_cosine_ops);
CREATE INDEX idx_jobs_work_model ON jobs (work_model);
CREATE INDEX idx_jobs_seniority ON jobs (seniority);
