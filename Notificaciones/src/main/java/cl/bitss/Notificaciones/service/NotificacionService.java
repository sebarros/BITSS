package cl.bitss.Notificaciones.service;

import cl.bitss.Notificaciones.dto.PedidoDTO;
import cl.bitss.Notificaciones.dto.UsuarioDTO;
import cl.bitss.Notificaciones.dto.VideojuegoDTO;
import cl.bitss.Notificaciones.exception.BusinessException;
import cl.bitss.Notificaciones.exception.ResourceNotFoundException;
import cl.bitss.Notificaciones.model.Notificacion;
import cl.bitss.Notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    private final WebClient clientUsuarios = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    private final WebClient clientGestion = WebClient.builder()
            .baseUrl("http://localhost:8084")
            .build();

    private final WebClient clientCatalogo = WebClient.builder()
            .baseUrl("http://localhost:8082")
            .build();

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    public List<Notificacion> obtenerTodos() {
        return repository.findAll();
    }

    public Optional<Notificacion> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Notificacion crear(Notificacion notificacion) {
        clientUsuarios.get()
                .uri("/usuarios/" + notificacion.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response
                        -> Mono.error(new BusinessException("El usuario no existe")))
                .onStatus(status -> status.is5xxServerError(), response
                        -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class)
                .block();

        if (notificacion.getPedidoId() != null) {
            PedidoDTO pedido = clientGestion.get()
                    .uri("/pedidos/" + notificacion.getPedidoId())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response
                            -> Mono.error(new BusinessException("El pedido indicado no existe")))
                    .onStatus(status -> status.is5xxServerError(), response
                            -> Mono.error(new BusinessException("Error en servicio Gestión")))
                    .bodyToMono(PedidoDTO.class)
                    .block();
            if (!pedido.getUsuarioId().equals(notificacion.getUsuarioId())) {
                throw new BusinessException("El pedido no pertenece al usuario indicado");
            }
        }

        if (notificacion.getVideojuegoId() != null) {
            clientCatalogo.get()
                    .uri("/videojuegos/" + notificacion.getVideojuegoId())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response
                            -> Mono.error(new BusinessException("El videojuego indicado no existe")))
                    .onStatus(status -> status.is5xxServerError(), response
                            -> Mono.error(new BusinessException("Error en servicio Catálogo")))
                    .bodyToMono(VideojuegoDTO.class)
                    .block();
        }
        return repository.save(notificacion);
    }

    public Notificacion actualizar(Long id, Notificacion datos) {
        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        notificacion.setUsuarioId(datos.getUsuarioId());
        notificacion.setPedidoId(datos.getPedidoId());
        notificacion.setVideojuegoId(datos.getVideojuegoId());
        notificacion.setTipo(datos.getTipo());
        notificacion.setMensaje(datos.getMensaje());
        notificacion.setEstado(datos.getEstado());
        return repository.save(notificacion);
    }

    public void eliminar(Long id) {
        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));
        repository.delete(notificacion);
    }

    public List<Notificacion> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Notificacion> obtenerPorTipo(String tipo) {
        return repository.findByTipo(tipo);
    }
}
