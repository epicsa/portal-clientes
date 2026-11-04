package com.imagina.demo.controller;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.service.ClienteService;
import com.imagina.demo.service.EmailInvalidoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public Page<Cliente> listar(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return clienteService.listarTodos(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteService.crear(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EmailInvalidoException.class)
    public ResponseEntity<ProblemDetail> emailInvalido(EmailInvalidoException ex) {
        ProblemDetail error = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "El email no tiene un formato válido");
        return ResponseEntity.badRequest().body(error);
    }
}
