package cl.bitss.ms_catalogo.repository;

import cl.bitss.ms_catalogo.model.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    List<Videojuego> findByCategoria(String categoria);
}