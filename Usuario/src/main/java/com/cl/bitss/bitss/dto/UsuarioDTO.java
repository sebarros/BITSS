package com.cl.bitss.bitss.dto;

public record UsuarioDTO(
        Long id,
        String nombre,
        String apellido,
        String correo
) {
}