package com.imagina.demo.controller;

import com.imagina.demo.service.ClienteDuplicadoException;
import com.imagina.demo.service.ClienteService;
import com.imagina.demo.service.EmailInvalidoException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
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
    public PagedModel<ClienteResponse> listar(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return new PagedModel<>(clienteService.listarTodos(pageable).map(ClienteMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ClienteMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ClienteResponse crear(@RequestBody ClienteRequest request) {
        return ClienteMapper.toResponse(clienteService.crear(ClienteMapper.toEntity(request)));
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

    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<ProblemDetail> clienteDuplicado(ClienteDuplicadoException ex) {
        ProblemDetail error = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Ya existe un cliente activo con ese email");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
