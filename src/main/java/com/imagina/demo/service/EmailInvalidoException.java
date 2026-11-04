package com.imagina.demo.service;

/**
 * Excepción de negocio: el email de un cliente no tiene un formato válido.
 */
public class EmailInvalidoException extends RuntimeException {

    public EmailInvalidoException() {
        super("El email no tiene un formato válido");
    }
}
