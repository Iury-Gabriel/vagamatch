package dev.iurygabriel.vagamatch.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Testes que PROVAM a Clean Architecture: as dependências só podem apontar para dentro.
 * Se alguém importar Spring/JPA no domínio, o build quebra.
 */
@AnalyzeClasses(packages = "dev.iurygabriel.vagamatch", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTest {

    @ArchTest
    static final ArchRule dominio_livre_de_framework =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "org.hibernate..")
                    .as("O domínio deve permanecer livre de framework (Spring/JPA/Hibernate)");

    @ArchTest
    static final ArchRule dominio_nao_depende_de_outras_camadas =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..")
                    .as("O domínio não pode depender de application/infrastructure/interfaces");

    @ArchTest
    static final ArchRule application_nao_depende_de_infra_nem_web =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..interfaces..")
                    .as("A aplicação orquestra o domínio via portas, sem conhecer infraestrutura/web");
}
