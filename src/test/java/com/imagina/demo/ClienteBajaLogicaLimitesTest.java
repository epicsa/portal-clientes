package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteBajaLogicaLimitesTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        clienteRepository.deleteAll();
    }

    @Test
    void trasLaBajaLaFilaSigueEnLaBaseDeDatosComoInactiva() {
        Long id = crearCliente("cliente001@example.com");

        assertThat(borrar(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Cliente> fila = clienteRepository.findById(id);
        assertThat(fila).isPresent();
        assertThat(fila.get().isActivo()).isFalse();
    }

    @Test
    void bajaDeUnIdQueNoExisteDevuelve204YNoCreaFilas() {
        Long id = crearCliente("cliente002@example.com");
        long filasAntes = clienteRepository.count();
        Long idInexistente = id + 1000;

        assertThat(borrar(idInexistente).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(clienteRepository.count()).isEqualTo(filasAntes);
        assertThat(clienteRepository.findById(idInexistente)).isEmpty();
    }

    @Test
    void dobleBajaDevuelve204LasDosVecesYDejaUnaSolaFilaInactiva() {
        Long id = crearCliente("cliente003@example.com");

        assertThat(borrar(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(borrar(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(clienteRepository.count()).isEqualTo(1);
        assertThat(clienteRepository.findById(id)).get()
                .extracting(Cliente::isActivo).isEqualTo(false);
    }

    @Test
    void getDeUnClienteDadoDeBajaDevuelve404() {
        Long id = crearCliente("cliente004@example.com");
        borrar(id);

        ResponseEntity<String> respuesta = restTemplate.getForEntity("/clientes/" + id, String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long crearCliente(String email) {
        ResponseEntity<Cliente> creado = restTemplate.postForEntity(
                "/clientes", new Cliente("Cliente Ficticio", email), Cliente.class);
        assertThat(creado.getStatusCode().is2xxSuccessful()).isTrue();
        return creado.getBody().getId();
    }

    private ResponseEntity<Void> borrar(Long id) {
        return restTemplate.exchange("/clientes/" + id, HttpMethod.DELETE, null, Void.class);
    }
}
