package com.imagina.demo.service;

import com.imagina.demo.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Normaliza y valida los datos de un cliente antes de guardarlo.
 */
@Component
public class ClienteValidator {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /**
     * Quita los espacios al principio y al final del email y después comprueba su formato.
     *
     * @throws EmailInvalidoException si el email es nulo o no tiene un formato válido
     */
    public void normalizarYValidar(Cliente cliente) {
        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }
        if (!esEmailValido(cliente.getEmail())) {
            throw new EmailInvalidoException();
        }
    }

    private boolean esEmailValido(String email) {
        return email != null && FORMATO_EMAIL.matcher(email).matches();
    }
}
