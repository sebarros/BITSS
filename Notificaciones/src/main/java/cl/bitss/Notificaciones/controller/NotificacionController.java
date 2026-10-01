package cl.bitss.Notificaciones.controller;

import cl.bitss.Notificaciones.model.Notificacion;
import cl.bitss.Notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notificacion> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@Valid @RequestBody Notificacion notificacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(notificacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        return ResponseEntity.ok(service.actualizar(id, notificacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada correctamente", "id", id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Notificacion> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }

    @GetMapping("/tipo/{tipo}")
    public List<Notificacion> obtenerPorTipo(@PathVariable String tipo) {
        return service.obtenerPorTipo(tipo);
    }
}
