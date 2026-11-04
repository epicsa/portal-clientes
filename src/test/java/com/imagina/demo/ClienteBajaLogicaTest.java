package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteBajaLogicaTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void clienteDadoDeBajaNoSeObtieneNiApareceEnElListado() {
        ResponseEntity<Cliente> creado = restTemplate.postForEntity(
                "/clientes", new Cliente("Baja Prueba", "baja.prueba@example.com"), Cliente.class);
        Long id = creado.getBody().getId();

        ResponseEntity<Void> baja = restTemplate.exchange(
                "/clientes/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(baja.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Cliente> obtenido = restTemplate.getForEntity("/clientes/" + id, Cliente.class);
        assertThat(obtenido.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<Cliente[]> listado = restTemplate.getForEntity("/clientes", Cliente[].class);
        assertThat(listado.getBody()).extracting(Cliente::getId).doesNotContain(id);
    }
}
