package cl.bitss.Carro.service;

import cl.bitss.Carro.dto.UsuarioDTO;
import cl.bitss.Carro.dto.VideojuegoDTO;
import cl.bitss.Carro.exception.BusinessException;
import cl.bitss.Carro.exception.ResourceNotFoundException;
import cl.bitss.Carro.model.Carrito;
import cl.bitss.Carro.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private final CarritoRepository repository;

    private final WebClient clientUsuarios = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    private final WebClient clientCatalogo = WebClient.builder()
            .baseUrl("http://localhost:8082")
            .build();

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    public List<Carrito> obtenerTodos() {
        return repository.findAll();
    }

    public Optional<Carrito> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Carrito crear(Carrito carrito) {
        clientUsuarios.get()
                .uri("/usuarios/" + carrito.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El usuario indicado no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class)
                .block();

        VideojuegoDTO juego = clientCatalogo.get()
                .uri("/videojuegos/" + carrito.getVideojuegoId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El videojuego indicado no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Catálogo")))
                .bodyToMono(VideojuegoDTO.class)
                .block();
        carrito.setNombreVideojuego(juego.getNombre());
        carrito.setCategoria(juego.getCategoria());
        carrito.setPrecio(juego.getPrecio());
        return repository.save(carrito);
    }

    @Transactional
    public Carrito actualizar(Long id, Carrito datos) {
        Carrito carrito = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + id));

        clientUsuarios.get().uri("/usuarios/" + datos.getUsuarioId()).retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El usuario indicado no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class).block();

        VideojuegoDTO juego = clientCatalogo.get().uri("/videojuegos/" + datos.getVideojuegoId()).retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El videojuego indicado no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Catálogo")))
                .bodyToMono(VideojuegoDTO.class).block();
        carrito.setUsuarioId(datos.getUsuarioId());
        carrito.setVideojuegoId(datos.getVideojuegoId());
        carrito.setNombreVideojuego(juego.getNombre());
        carrito.setCategoria(juego.getCategoria());
        carrito.setPrecio(juego.getPrecio());
        return repository.save(carrito);
    }

    public void eliminar(Long id) {
        Carrito carrito = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + id));
        repository.delete(carrito);
    }

    public List<Carrito> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
}
