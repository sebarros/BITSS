
package com.cl.bitss.bitss.controller;
import com.cl.bitss.bitss.model.Usuario;
import com.cl.bitss.bitss.service.UsuarioService;
import com.cl.bitss.bitss.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> datos) {

        String correo = datos.getOrDefault("correo", "");
        String password = datos.getOrDefault("password", "");

        try {
            Usuario usuario = usuarioService.autenticar(correo, password);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Inicio de sesión exitoso",
                    "token", JwtUtil.generate(usuario.getCorreo()),
                    "tipo", "Bearer",
                    "expiraEnSegundos", 3600,
                    "usuarioId", usuario.getId(),
                    "correo", usuario.getCorreo()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje", "Correo o contraseña incorrectos"
                    ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of(
                "mensaje", "Sesión cerrada. Elimina el token del cliente."
        ));
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarUsuario(
            @PathVariable Long id) {

        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario encontrado correctamente");
            respuesta.put("id", usuario.getId());
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("apellido", usuario.getApellido());
            respuesta.put("correo", usuario.getCorreo());

            return ResponseEntity.ok(respuesta);

        } catch (com.cl.bitss.bitss.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", "No se encontró un usuario con ID: " + id
                    ));
        }
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Map<String, Object>> buscarPorCorreo(
            @PathVariable String correo) {

        return usuarioService.buscarPorCorreo(correo)
                .<ResponseEntity<Map<String, Object>>>map(usuario ->
                        ResponseEntity.ok(Map.of(
                                "mensaje", "Usuario encontrado correctamente",
                                "id", usuario.getId(),
                                "nombre", usuario.getNombre(),
                                "apellido", usuario.getApellido(),
                                "correo", usuario.getCorreo()
                        )))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "mensaje", "No se encontró un usuario con el correo: " + correo
                                )));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarUsuario(
            @Valid @RequestBody Usuario usuario) {

        try {
            Usuario registrado = usuarioService.registrarUsuario(usuario);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Usuario registrado correctamente",
                            "id", registrado.getId(),
                            "nombre", registrado.getNombre(),
                            "apellido", registrado.getApellido(),
                            "correo", registrado.getCorreo()
                    ));

        } catch (com.cl.bitss.bitss.exception.BusinessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario actualizado correctamente",
                    "id", actualizado.getId(),
                    "nombre", actualizado.getNombre(),
                    "apellido", actualizado.getApellido(),
                    "correo", actualizado.getCorreo()
            ));

        } catch (com.cl.bitss.bitss.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", "No se encontró un usuario con ID: " + id
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(
            @PathVariable Long id) {

        try {
            usuarioService.eliminarUsuario(id);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario eliminado correctamente",
                    "id", id
            ));

        } catch (com.cl.bitss.bitss.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", "No se encontró un usuario con ID: " + id
                    ));
        }
    }
}