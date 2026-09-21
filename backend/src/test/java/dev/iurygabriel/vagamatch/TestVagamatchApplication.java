package dev.iurygabriel.vagamatch;

import org.springframework.boot.SpringApplication;

public class TestVagamatchApplication {

	public static void main(String[] args) {
		SpringApplication.from(VagamatchApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
