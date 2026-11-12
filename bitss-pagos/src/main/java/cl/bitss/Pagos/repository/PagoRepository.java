package cl.bitss.Pagos.repository;

import cl.bitss.Pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByUsuarioId(Long usuarioId);
    List<Pago> findByPedidoId(Long pedidoId);
}