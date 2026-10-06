package cl.bitss.ms_catalogo.service;

import cl.bitss.ms_catalogo.exception.ResourceNotFoundException;
import cl.bitss.ms_catalogo.model.Videojuego;
import cl.bitss.ms_catalogo.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoService {
    private final VideojuegoRepository repository;

    public VideojuegoService(VideojuegoRepository repository) {
        this.repository = repository;
    }

    public List<Videojuego> listar() {
        return repository.findAll();
    }

    public Videojuego obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Videojuego no encontrado con ID: " + id));
    }

    public Videojuego crear(Videojuego videojuego) {
        return repository.save(videojuego);
    }

    public Videojuego actualizar(Long id, Videojuego datos) {
        Videojuego videojuego = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Videojuego no encontrado con ID: " + id));
        videojuego.setNombre(datos.getNombre());
        videojuego.setCategoria(datos.getCategoria());
        videojuego.setPrecio(datos.getPrecio());
        return repository.save(videojuego);
    }

    public void eliminar(Long id) {
        Videojuego videojuego = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Videojuego no encontrado con ID: " + id));
        repository.delete(videojuego);
    }

    public List<Videojuego> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }
}
