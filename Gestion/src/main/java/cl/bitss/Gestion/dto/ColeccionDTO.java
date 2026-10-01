package cl.bitss.Gestion.dto;

public class ColeccionDTO {
    private Long usuarioId;
    private Long videojuegoId;
    private String nombreVideojuego;
    private String categoria;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getVideojuegoId() {
        return videojuegoId;
    }

    public void setVideojuegoId(Long videojuegoId) {
        this.videojuegoId = videojuegoId;
    }

    public String getNombreVideojuego() {
        return nombreVideojuego;
    }

    public void setNombreVideojuego(String nombreVideojuego) {
        this.nombreVideojuego = nombreVideojuego;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
