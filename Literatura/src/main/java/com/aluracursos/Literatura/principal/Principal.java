package com.aluracursos.Literatura.principal;

import com.aluracursos.Literatura.model.Autor;
import com.aluracursos.Literatura.model.Datos;
import com.aluracursos.Literatura.model.DatosLibros;
import com.aluracursos.Literatura.model.DatosAutor;
import com.aluracursos.Literatura.model.Libro;
import com.aluracursos.Literatura.repository.AutorRepository;
import com.aluracursos.Literatura.repository.LibroRepository;
import com.aluracursos.Literatura.service.ConsumoAPI;
import com.aluracursos.Literatura.service.ConvierteDatos;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private List<Autor> autores;
    private List<Libro> libros;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {

        var opcion = -1;
            while (opcion != 0) {
                var menu = """
                        Elija la opción a través de su número:
                        1- Buscar libro por titulo
                        2- Listar libros registrados
                        3- Listar autores registrados
                        4- Listar autores vivos en un determinado año
                        5- Listar libros por idioma
                        
                        0) Salir
                        """;
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listaLibrosRegistrados();
                        break;
                    case 3:
                        listaAutoresRegistrados();
                        break;
                    case 4:
                        listaAutoresVivosEnDeterminadoAno();
                        break;
                    case 5:
                        listaLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;

                }
            }
    }

    private Datos buscarDatosLibros() {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        var libro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + libro.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private Libro agregarLibroBD(DatosLibros datosLibros, Autor autor) {
        Libro libro = new Libro(datosLibros, autor);
        return libroRepository.save(libro);

    }

    private void buscarLibroPorTitulo() {
        Datos datos = buscarDatosLibros();

        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibros.autor().get(0);
            Libro libroBuscado = libroRepository.findByTitulo(datosLibros.titulo());

            if (libroBuscado != null) {
                System.out.println(libroBuscado);
                System.out.println("El libro ya existe en la base de datos.");

            } else {
                Autor autorBuscado = autorRepository.findByNombreIgnoreCase(datosAutor.nombre());

                if (autorBuscado == null) {
                    Autor autor = new Autor(datosAutor);
                    autorRepository.save(autor);
                    Libro libro = agregarLibroBD(datosLibros, autor);
                    System.out.println(libro);
                } else {
                    Libro libro = agregarLibroBD(datosLibros, autorBuscado);
                    System.out.println(libro);
                }
            }
        } else {
            System.out.println("El libro buscado no se encuentra. Pruebe con otro.");
        }
    }

    private void listaLibrosRegistrados() {
        libros = libroRepository.findAll();
        if (!libros.isEmpty()) {
            libros.stream().forEach(System.out::println);
        } else {
            System.out.println("No hay ningún Libro registrado.");
        }
    }

    private void listaAutoresRegistrados() {
        autores = autorRepository.findAll();
        if (!autores.isEmpty()) {
            autores.stream().forEach(System.out::println);
        } else {
            System.out.println("No hay ningún Autor registrado");
        }
    }

    private void listaAutoresVivosEnDeterminadoAno() {
        System.out.println("Ingrese el año en el que quiere saber los autores vivos: ");
        int fecha = teclado.nextInt();
        try {
            List<Autor> autoresVivosEnCiertaFecha = autorRepository.autorVivoAno(fecha);
            if (!autoresVivosEnCiertaFecha.isEmpty()) {
                autoresVivosEnCiertaFecha.stream().forEach(System.out::println);
            } else {
                System.out.println("No existen Autores vivos en esos años.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void listaLibrosPorIdioma() {
        System.out.println("""
                1- es- Español
                2- en- Inglés
                3- fr- Francés
                4- pt- Portugués
                
                5- Regresar al menú principal
                """);
        int opcion;
        opcion = teclado.nextInt();
        teclado.nextLine();
        switch (opcion) {
            case 1:
                libros = libroRepository.findByIdiomasContaining("es");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Español.");
                }
                break;
            case 2:
                libros = libroRepository.findByIdiomasContaining("en");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Inglés.");
                }
                break;
            case 3:
                libros = libroRepository.findByIdiomasContaining("fr");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Francés.");
                }
                break;
            case 4:
                libros = libroRepository.findByIdiomasContaining("pt");
                if (!libros.isEmpty()) {
                    libros.stream().forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Portugués.");
                }
                break;
            case 5:
                muestraElMenu();
                break;
            default:
                System.out.println("La opción seleccionada no es válida.");
        }
    }
}