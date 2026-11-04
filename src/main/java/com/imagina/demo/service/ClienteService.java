package com.imagina.demo.service;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Page<Cliente> listarTodos(Pageable pageable) {
        return clienteRepository.findByActivoTrue(pageable);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findByIdAndActivoTrue(id);
    }

    public Cliente crear(Cliente cliente) {
        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }
        if (!esEmailValido(cliente.getEmail())) {
            throw new EmailInvalidoException();
        }
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        });
    }

    private boolean esEmailValido(String email) {
        return email != null && FORMATO_EMAIL.matcher(email).matches();
    }
}
