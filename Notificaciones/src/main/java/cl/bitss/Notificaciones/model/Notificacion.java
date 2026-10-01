package cl.bitss.Notificaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuarioId es obligatorio")
    @Positive(message = "El usuarioId debe ser mayor que 0")
    private Long usuarioId;

    @Positive(message = "El pedidoId debe ser mayor que 0")
    private Long pedidoId;

    @Positive(message = "El videojuegoId debe ser mayor que 0")
    private Long videojuegoId;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
    private String tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 255, message = "El mensaje no puede superar los 255 caracteres")
    private String mensaje;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
    private String estado;

    public Notificacion() {
    }

    public Notificacion(Long usuarioId, Long pedidoId, Long videojuegoId, String tipo, String mensaje, String estado) {
        this.usuarioId = usuarioId;
        this.pedidoId = pedidoId;
        this.videojuegoId = videojuegoId;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getVideojuegoId() {
        return videojuegoId;
    }

    public void setVideojuegoId(Long videojuegoId) {
        this.videojuegoId = videojuegoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
