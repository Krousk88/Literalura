package com.alura.Literalura.service;

import com.alura.Literalura.dto.DatosAutor;
import com.alura.Literalura.entity.Autor;
import com.alura.Literalura.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {


    private final AutorRepository repo;

    public AutorService (AutorRepository repo) {
        this.repo = repo;
    }


    public Autor obtenerOCrear(DatosAutor dto) {
        if (dto == null || dto.name() == null) {
            throw new IllegalArgumentException("DatosAutor o nombre es null");
        }

        return repo.findByNombre(dto.name())
                .orElseGet(() -> {
                    Autor autor = new Autor();
                    autor.setNombre(dto.name());
                    autor.setAnioDeNacimiento(dto.birthYear());
                    autor.setAnioDeFallecimiento(dto.deathYea());
                    return repo.save(autor);
                });

    }

    public List<Autor> listarAutores() {
        return repo.listarAutores();
    }

    public Optional<Autor> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser null o vacío");
        }
        return repo.buscarPorNombre(nombre);
    }

    public  List<Autor> autoresPorAnioDeNacimiento(Integer anio) {
        return repo.autoresPorAnioDeNacimiento(anio);
    }


}