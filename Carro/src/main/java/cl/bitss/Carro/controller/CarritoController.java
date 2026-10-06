package cl.bitss.Carro.controller;

import cl.bitss.Carro.model.Carrito;
import cl.bitss.Carro.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrito")
public class CarritoController {
    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Carrito> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Carrito> crear(@Valid @RequestBody Carrito carrito) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(carrito));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(@PathVariable Long id, @Valid @RequestBody Carrito carrito) {
        return ResponseEntity.ok(service.actualizar(id, carrito));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Juego eliminado del carrito", "id", id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Carrito> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }
}