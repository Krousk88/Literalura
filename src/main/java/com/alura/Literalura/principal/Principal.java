package com.alura.Literalura.principal;

import com.alura.Literalura.Enum.Busqueda;
import com.alura.Literalura.Enum.Idiomas;
import com.alura.Literalura.dto.DatosAutor;
import com.alura.Literalura.dto.DatosBook;
import com.alura.Literalura.entity.Autor;
import com.alura.Literalura.entity.Libro;
import com.alura.Literalura.mapper.LibroMapper;
import com.alura.Literalura.service.AutorService;
import com.alura.Literalura.service.ConsumoAPI;
import com.alura.Literalura.service.ConvertirDatos;
import com.alura.Literalura.service.LibroService;


import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.util.*;

public class Principal {
    private final ConsumoAPI consumoAPI  =  new ConsumoAPI();
    private final ConvertirDatos convertirDatos =  new ConvertirDatos();
    private final Scanner sc = new Scanner(System.in);
    private final LibroMapper libroMapper = new LibroMapper();
    private final AutorService autorService;
    private final LibroService librosService;
    private static final String MENSAJE_LIBROS_NO_REGISTRADOS = "No hay libros registrados en la base de datos.";
    private static final String MENSAJE_VALOR_NULO = "Error, valor ingresado no puede estar vacío";


    public Principal(AutorService autorService, LibroService librosService) {
        this.autorService = autorService;
        this.librosService = librosService;
    }

    public void mostarMenu() {
        var opcion = -1;
        do {
            printMenu();
            try {
                opcion = Integer.parseInt(sc.nextLine());
                ejecutarOpcion(opcion);
            }catch (NumberFormatException e){
                System.out.println("Por favor ingrese un numero válido");
            }

        }while (opcion != 0);
    }

    public void printMenu() {
        System.out.println("""
                ----------------------------------------
                 📖  LITERALURA - MENÚ PRINCIPAL  📖
                ----------------------------------------
                 1️⃣ - Buscar libro por título
                 2️⃣ - Listar libros
                 3️⃣ - Listar autores
                 4️⃣ - Listar libros por idioma
                 5️⃣ - Buscar autor
                 6️⃣ - Top 10 libros mas descargados
                 7️⃣ - Estadísticas de descargas
                 8️⃣ - Listar Autores vivos por un año determinado
                 0️⃣  Salir
                ----------------------------------------
                Opción:
        """);
        System.out.print("Seleccione una opción > ");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> buscarLibroPorTitulo();
            case 2 -> listarLibrosRegistrados();
            case 3 -> listarAutoresRegistrados();
            case 4 -> listarLibrosPorIdioma();
            case 5 -> buscarAutorPorNombre();
            case 6 -> obtenerTop10LibrosDescargados();
            case 7 -> obtenerEstadisticasDeDescargas();
            case 8 -> listarAutoresVivosPorAnio();
            case 0 -> System.out.println("Saliendo del programa. ¡Hasta luego!");
            default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("""
                ----------------------------------------
                        Buscar Libro por Título
                ----------------------------------------
                """);
        System.out.print("Ingrese el titulo del libro a buscar: ");
        String titulo = sc.nextLine();
        if (titulo.isBlank()){
            System.out.println(MENSAJE_VALOR_NULO);
            return;
        }
        System.out.println("...Buscando libro");
        DatosBook datosBook;
        try {
            datosBook = getDatosBookPorTitulo(titulo);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (datosBook != null) {
            DatosAutor autorDTO = libroMapper.obtenerAutorPrincipal(datosBook.autores());
            Idiomas idiomas = Idiomas.obtenerPorNombre(libroMapper.obtenerLenguajePrincipal(datosBook.languages()));
            Autor autor;
            if (autorDTO != null) {
                autor = autorService.obtenerOCrear(autorDTO);
                Libro libro = libroMapper.toEntity(datosBook, autor, idiomas);
                Optional<Libro> libroBuscado = librosService.buscarPorTitulo(libro.getTitulo());
                if (libroBuscado.isPresent()) {
                    System.out.println("El libro ya existía en la base de datos, no se guardo una nueva entrada\n" + libroBuscado.get());
                } else {
                    librosService.guardarLibro(libro);
                    autor.agregarLibro(libro);
                    System.out.println("Libro guardado exitosamente:\n" + libro);
                }
            } else {
                System.out.println("Autor no encontrado para el libro proporcionado.");

            }
        }
    }

    private DatosBook getDatosBookPorTitulo(String titulo) {
        String json = consumoAPI.obtenerDatos( titulo, Busqueda.LIBRO_AUTOR.getCodigo());
        return convertirDatos.convertirDatos(json,DatosBook.class) ;
    }

    private void listarLibrosRegistrados() {
        System.out.println("""
                 ----------------------------------------
                         Mostrar Libros Registrados
                ----------------------------------------
                """);
        List<Libro> libroList = librosService.listarLibros();
        if (libroList.isEmpty()) {
            System.out.println(MENSAJE_LIBROS_NO_REGISTRADOS);
        } else {
            libroList.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        System.out.println("""
                 ----------------------------------------
                        Mostrar Autores Registrados
                ----------------------------------------
                """);
        List<Autor> autorList = autorService.listarAutores();
        if (autorList.isEmpty()) {
            System.out.println(MENSAJE_LIBROS_NO_REGISTRADOS);
        } else {
            autorList.forEach(System.out::println);
        }

    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                ----------------------------------------
                         Mostrar Libros por Idioma
                ----------------------------------------
                """);
        System.out.println("Idiomas disponibles: ");
        for (Idiomas idiomas: Idiomas.values()) {
            if (idiomas.getCodigo().equalsIgnoreCase("und")) {
                continue;
            }
            System.out.printf(" - %s (%s)%n", idiomas.name(), idiomas.getCodigo());
        }
        System.out.print("Ingrese el código del idioma: ");
        String codigoIdioma = sc.nextLine();

        if (codigoIdioma.isBlank()) {
            System.out.println(MENSAJE_VALOR_NULO);
        }
        Idiomas idiomaSeleccionado = Idiomas.obtenerPorNombre(codigoIdioma);
        var librosPorIdioma = librosService.listarLibrosPorIdioma(idiomaSeleccionado);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma seleccionado.");
        } else {
            librosPorIdioma.forEach(System.out::println);
        }
    }

    private void buscarAutorPorNombre() {
        System.out.println("""
                ----------------------------------------
                         Buscar Autor por nombre
                ----------------------------------------
                """);
        System.out.print("Ingrese el nombre del autor a buscar:");
        String nombreAutor = sc.nextLine();
        System.out.println("...Buscando autor");
        Optional<Autor> autorBuscado;
        try {
            autorBuscado = autorService.buscarPorNombre(nombreAutor);
        } catch ( IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (autorBuscado.isPresent()) {
            System.out.println("Autor encontrado:\n" + autorBuscado.get());
        }else  {
            System.out.println( "Autor no encontrado con el nombre proporcionado.");
        }
    }

    private void obtenerTop10LibrosDescargados() {
        System.out.println("""
                ----------------------------------------
                    Top 10 de libros mas descargados  ️️️️️️️️️️️️️️️️
                ----------------------------------------
                """);
        List<Libro>libroList = librosService.obtenerTop10Descargas();
        if (libroList.isEmpty()) {
            System.out.println("No hay autores registrados");
        } else {
            libroList.forEach(System.out::println);
        }
    }

    private void obtenerEstadisticasDeDescargas() {

        List <Libro> libroList = librosService.listarLibros();
        if (libroList.isEmpty()) {
            System.out.println(MENSAJE_LIBROS_NO_REGISTRADOS);
            return;
        }
        System.out.println("""
                ----------------------------------------
                        Estadísticas de descargas
                ----------------------------------------
                """);

        IntSummaryStatistics ds = libroList.stream()
                .filter(libro -> libro.getDownloads()> 0)
                .collect(Collectors.summarizingInt(Libro::getDownloads));
        System.out.println("Cantidad de Libros: " + ds.getCount());
        System.out.println("Descarga mínima: " + ds.getMin());
        System.out.println("Descarga máxima: " + ds.getMax());
        System.out.println("Descarga promedio: " + String.format("%.2f",ds.getAverage()));
        System.out.println("Sumatoria de Descargas: " + ds.getSum());

    }

    private void listarAutoresVivosPorAnio() {
        System.out.println("""
                ----------------------------------------
                       Listar Autores vivos por año
                ----------------------------------------
                """);
        System.out.print("Ingrese el año para listar autores vivos: ");
        int anio = Integer.parseInt(sc.nextLine());
        System.out.println("...Buscando autores vivos en el año " + anio);
        List<Autor> autorList = autorService.autoresPorAnioDeNacimiento(anio);
        if (autorList.isEmpty()) {
            System.out.println("No hay autores vivos registrados en el año proporcionado.");
        } else {
            autorList.forEach(System.out::println);
        }
    }


}
