package com.alura.Literalura;

import com.alura.Literalura.principal.Principal;
import com.alura.Literalura.service.AutorService;
import com.alura.Literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private AutorService autorService;

	@Autowired
	private LibroService librosService;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Principal principal = new Principal(autorService,librosService);
		principal.mostarMenu();

	}
}
