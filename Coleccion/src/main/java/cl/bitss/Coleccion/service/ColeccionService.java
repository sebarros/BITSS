package cl.bitss.Coleccion.service;

import cl.bitss.Coleccion.dto.UsuarioDTO;
import cl.bitss.Coleccion.exception.BusinessException;
import cl.bitss.Coleccion.exception.ResourceNotFoundException;
import cl.bitss.Coleccion.model.Coleccion;
import cl.bitss.Coleccion.repository.ColeccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class ColeccionService {

    private final ColeccionRepository repository;

    private final WebClient clientUsuarios = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    public ColeccionService(ColeccionRepository repository) {
        this.repository = repository;
    }

    public List<Coleccion> obtenerTodos() {
        return repository.findAll();
    }

    public Optional<Coleccion> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Coleccion crear(Coleccion coleccion) {
        clientUsuarios.get()
                .uri("/usuarios/" + coleccion.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El usuario no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class)
                .block();
        return repository.save(coleccion);
    }

    @Transactional
    public Coleccion actualizar(Long id, Coleccion datos) {
        Coleccion coleccion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Juego no encontrado en la colección con ID: " + id));

        clientUsuarios.get()
                .uri("/usuarios/" + datos.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El usuario no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class)
                .block();

        coleccion.setUsuarioId(datos.getUsuarioId());
        coleccion.setVideojuegoId(datos.getVideojuegoId());
        coleccion.setNombreVideojuego(datos.getNombreVideojuego());
        coleccion.setCategoria(datos.getCategoria());
        return repository.save(coleccion);
    }

    public void eliminar(Long id) {
        Coleccion coleccion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Juego no encontrado en la colección con ID: " + id));
        repository.delete(coleccion);
    }

    public List<Coleccion> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Coleccion> obtenerPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }
}
