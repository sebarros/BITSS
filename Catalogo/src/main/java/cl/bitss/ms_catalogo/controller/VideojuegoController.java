package cl.bitss.ms_catalogo.controller;

import cl.bitss.ms_catalogo.model.Videojuego;
import cl.bitss.ms_catalogo.service.VideojuegoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/videojuegos")
public class VideojuegoController {
    private final VideojuegoService service;

    public VideojuegoController(VideojuegoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Videojuego> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Videojuego> crear(@Valid @RequestBody Videojuego videojuego) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(videojuego));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Videojuego> actualizar(@PathVariable Long id, @Valid @RequestBody Videojuego videojuego) {
        return ResponseEntity.ok(service.actualizar(id, videojuego));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Videojuego eliminado correctamente", "id", id));
    }

    @GetMapping("/categoria/{categoria}")
    public List<Videojuego> buscarPorCategoria(@PathVariable String categoria) {
        return service.buscarPorCategoria(categoria);
    }
}