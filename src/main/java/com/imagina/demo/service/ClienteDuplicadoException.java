package com.imagina.demo.service;

/**
 * Excepción de negocio: ya existe un cliente activo con el mismo email.
 */
public class ClienteDuplicadoException extends RuntimeException {

    public ClienteDuplicadoException() {
        super("Ya existe un cliente activo con ese email");
    }
}
