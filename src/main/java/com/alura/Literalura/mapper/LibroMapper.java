package com.alura.Literalura.mapper;

import com.alura.Literalura.Enum.Idiomas;
import com.alura.Literalura.dto.DatosAutor;
import com.alura.Literalura.dto.DatosBook;
import com.alura.Literalura.entity.Autor;
import com.alura.Literalura.entity.Libro;

import java.util.List;

public class LibroMapper {

    public DatosAutor obtenerAutorPrincipal(List<DatosAutor> autores) {
        if (autores == null || autores.isEmpty()) {
            return null;
        }
        return autores.get(0);
    }

    public String obtenerLenguajePrincipal(List<String> lenguajes) {
        if (lenguajes == null || lenguajes.isEmpty()) {
            return null;
        }
        return lenguajes.get(0);
    }

    public Libro toEntity(DatosBook dto, Autor autor, Idiomas idioma) {
        Libro libro = new Libro();
        libro.setTitulo(dto.title());
        libro.setAutor(autor);
        libro.setIdioma(idioma);
        libro.setDownloads(dto.download());
        return libro;
    }

    public <DatosBook> Libro toEntity(DatosBook datosBook, Autor autor, Idiomas idiomas) {
        return null;
    }
}


