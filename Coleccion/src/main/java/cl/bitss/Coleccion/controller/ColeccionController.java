package cl.bitss.Coleccion.controller;

import cl.bitss.Coleccion.model.Coleccion;
import cl.bitss.Coleccion.service.ColeccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coleccion")
public class ColeccionController {
    private final ColeccionService service;

    public ColeccionController(ColeccionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Coleccion> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coleccion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Coleccion> crear(@Valid @RequestBody Coleccion coleccion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(coleccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coleccion> actualizar(@PathVariable Long id, @Valid @RequestBody Coleccion coleccion) {
        return ResponseEntity.ok(service.actualizar(id, coleccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Juego eliminado de la colección", "id", id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Coleccion> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Coleccion> obtenerPorCategoria(@PathVariable String categoria) {
        return service.obtenerPorCategoria(categoria);
    }
}