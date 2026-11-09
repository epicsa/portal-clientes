package com.imagina.demo.controller;

import com.imagina.demo.model.Cliente;

/**
 * Conversión entre los DTOs de la API y la entidad Cliente.
 */
public final class ClienteMapper {

    private ClienteMapper() {
    }

    /** Crea una entidad nueva: el id y el estado no se toman nunca de la petición. */
    public static Cliente toEntity(ClienteRequest request) {
        return new Cliente(request.nombre(), request.email());
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getEmail());
    }
}
