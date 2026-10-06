package cl.bitss.Pagos.controller;

import cl.bitss.Pagos.model.Pago;
import cl.bitss.Pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pago> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Pago> crear(@Valid @RequestBody Pago pago) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(pago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Long id, @Valid @RequestBody Pago pago) {
        return ResponseEntity.ok(service.actualizar(id, pago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Pago eliminado correctamente", "id", id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Pago> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Pago> obtenerPorPedido(@PathVariable Long pedidoId) {
        return service.obtenerPorPedido(pedidoId);
    }
}
