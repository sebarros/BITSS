package cl.bitss.Gestion.service;

import cl.bitss.Gestion.dto.CarritoDTO;
import cl.bitss.Gestion.dto.ColeccionDTO;
import cl.bitss.Gestion.dto.PagoDTO;
import cl.bitss.Gestion.dto.UsuarioDTO;
import cl.bitss.Gestion.exception.BusinessException;
import cl.bitss.Gestion.exception.ResourceNotFoundException;
import cl.bitss.Gestion.model.Pedido;
import cl.bitss.Gestion.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    private final WebClient clientUsuarios = WebClient.builder().baseUrl("http://localhost:8081").build();

    private final WebClient clientCarrito = WebClient.builder().baseUrl("http://localhost:8083").build();

    private final WebClient clientPagos = WebClient.builder().baseUrl("http://localhost:8085").build();

    private final WebClient clientColeccion = WebClient.builder().baseUrl("http://localhost:8086").build();

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public List<Pedido> obtenerTodos() {
        return repository.findAll();
    }

    public Pedido obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    @Transactional
    public Pedido crear(Pedido pedido) {
        clientUsuarios.get()
                .uri("/usuarios/" + pedido.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response-> Mono.error(new BusinessException("El usuario no existe")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new BusinessException("Error en servicio Usuarios")))
                .bodyToMono(UsuarioDTO.class)
                .block();

        List<CarritoDTO> carrito = clientCarrito.get()
                .uri("/carrito/usuario/" + pedido.getUsuarioId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new BusinessException("No fue posible obtener el carrito")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new BusinessException("Error en servicio Carrito")))
                .bodyToFlux(CarritoDTO.class)
                .collectList()
                .block();

        if (carrito == null || carrito.isEmpty()) {
            throw new BusinessException("El carrito está vacío");
        }

        double total = carrito.stream().mapToDouble(CarritoDTO::getPrecio).sum();
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");
        Pedido pedidoGuardado = repository.save(pedido);
        PagoDTO pago = new PagoDTO();
        pago.setPedidoId(pedidoGuardado.getId());
        pago.setUsuarioId(pedidoGuardado.getUsuarioId());
        pago.setMonto(total);
        pago.setMetodoPago("TARJETA");
        pago.setEstado("APROBADO");

        clientPagos.post()
                .uri("/pagos")
                .bodyValue(pago)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new BusinessException("El pago fue rechazado")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new BusinessException("Error en servicio Pagos")))
                .bodyToMono(PagoDTO.class)
                .block();

        for (CarritoDTO item : carrito) {
            ColeccionDTO coleccion = new ColeccionDTO();
            coleccion.setUsuarioId(pedidoGuardado.getUsuarioId());
            coleccion.setVideojuegoId(item.getVideojuegoId());
            coleccion.setNombreVideojuego(item.getNombreVideojuego());
            coleccion.setCategoria(item.getCategoria());

            clientColeccion.post()
                    .uri("/coleccion")
                    .bodyValue(coleccion)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new BusinessException("No fue posible agregar el juego a la colección")))
                    .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new BusinessException("Error en servicio Colección")))
                    .bodyToMono(ColeccionDTO.class)
                    .block();
        }
        pedidoGuardado.setEstado("COMPLETADO");
        return repository.save(pedidoGuardado);
    }

    public Pedido actualizar(Long id, Pedido datos) {
        Pedido pedido = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        pedido.setUsuarioId(datos.getUsuarioId());
        pedido.setTotal(datos.getTotal());
        pedido.setEstado(datos.getEstado());
        return repository.save(pedido);
    }

    public void eliminar(Long id) {
        Pedido pedido = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        repository.delete(pedido);
    }

    public List<Pedido> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
}
