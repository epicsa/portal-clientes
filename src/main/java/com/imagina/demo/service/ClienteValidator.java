package com.imagina.demo.service;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Normaliza y valida los datos de un cliente antes de guardarlo.
 */
@Component
public class ClienteValidator {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final ClienteRepository clienteRepository;

    public ClienteValidator(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Quita los espacios al principio y al final del email, comprueba su formato y que
     * no exista otro cliente activo con el mismo email (sin distinguir mayúsculas).
     *
     * @throws EmailInvalidoException si el email es nulo o no tiene un formato válido
     * @throws ClienteDuplicadoException si ya existe un cliente activo con ese email
     */
    public void normalizarYValidar(Cliente cliente) {
        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }
        if (!esEmailValido(cliente.getEmail())) {
            throw new EmailInvalidoException();
        }
        if (clienteRepository.existsByEmailIgnoreCaseAndActivoTrue(cliente.getEmail())) {
            throw new ClienteDuplicadoException();
        }
    }

    private boolean esEmailValido(String email) {
        return email != null && FORMATO_EMAIL.matcher(email).matches();
    }
}
