package dev.iurygabriel.vagamatch;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		// Imagem com a extensão pgvector, compatível com o driver do Postgres.
		return new PostgreSQLContainer(
				DockerImageName.parse("pgvector/pgvector:pg16")
						.asCompatibleSubstituteFor("postgres"));
	}

}
