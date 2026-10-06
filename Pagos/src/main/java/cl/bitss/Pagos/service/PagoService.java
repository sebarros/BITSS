package cl.bitss.Pagos.service;

import cl.bitss.Pagos.exception.BusinessException;
import cl.bitss.Pagos.exception.ResourceNotFoundException;
import cl.bitss.Pagos.model.Pago;
import cl.bitss.Pagos.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository repository;

    private final boolean pasarelaActiva;

    private final WebClient clientPasarela = WebClient.builder().baseUrl("http://localhost:8090").build();

    public PagoService(PagoRepository repository, @Value("${pasarela.externa.activa:false}") boolean pasarelaActiva) {
        this.repository = repository;
        this.pasarelaActiva = pasarelaActiva;
    }

    public List<Pago> obtenerTodos() {
        return repository.findAll();
    }

    public Pago obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
    }

    @Transactional
    public Pago crear(Pago pago) {
        if (pasarelaActiva) {
            clientPasarela.post()
                    .uri("/pagos")
                    .bodyValue(pago)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new BusinessException("La pasarela rechazó el pago")))
                    .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new BusinessException("La pasarela de pago no está disponible")))
                    .toBodilessEntity()
                    .block();
        }
        pago.setEstado("APROBADO");
        return repository.save(pago);
    }

    public Pago actualizar(Long id, Pago datos) {
        Pago pago = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        pago.setPedidoId(datos.getPedidoId());
        pago.setUsuarioId(datos.getUsuarioId());
        pago.setMonto(datos.getMonto());
        pago.setMetodoPago(datos.getMetodoPago());
        pago.setEstado(datos.getEstado());
        return repository.save(pago);
    }

    public void eliminar(Long id) {
        Pago pago = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        repository.delete(pago);
    }

    public List<Pago> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Pago> obtenerPorPedido(Long pedidoId) {
        return repository.findByPedidoId(pedidoId);
    }
}