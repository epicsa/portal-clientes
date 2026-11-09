package com.imagina.demo.service;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteValidator clienteValidator;

    public ClienteService(ClienteRepository clienteRepository, ClienteValidator clienteValidator) {
        this.clienteRepository = clienteRepository;
        this.clienteValidator = clienteValidator;
    }

    public Page<Cliente> listarTodos(Pageable pageable) {
        return clienteRepository.findByActivoTrue(pageable);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findByIdAndActivoTrue(id);
    }

    public Cliente crear(Cliente cliente) {
        clienteValidator.normalizarYValidar(cliente);
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        });
    }
}
