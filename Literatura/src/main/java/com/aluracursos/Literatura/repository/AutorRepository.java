package com.aluracursos.Literatura.repository;

import com.aluracursos.Literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    Autor findByNombreIgnoreCase(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :fecha AND (a.fechaFallecimiento IS NULL OR a.fechaFallecimiento >= :fecha)")
    List<Autor> autorVivoAno(@Param("fecha") int fecha);
}
