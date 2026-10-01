package cl.bitss.Coleccion.repository;

import cl.bitss.Coleccion.model.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ColeccionRepository extends JpaRepository<Coleccion, Long> {
    List<Coleccion> findByUsuarioId(Long usuarioId);
    List<Coleccion> findByCategoria(String categoria);
}