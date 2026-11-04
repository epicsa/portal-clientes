package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteEmailTrimTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void emailConEspaciosAlrededorSeGuardaSinEllos() {
        Cliente nuevo = new Cliente("Eva Espacios", "  eva.espacios@example.com  ");

        ResponseEntity<Cliente> creado = restTemplate.postForEntity("/clientes", nuevo, Cliente.class);
        assertThat(creado.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(creado.getBody().getEmail()).isEqualTo("eva.espacios@example.com");

        ResponseEntity<Cliente> obtenido = restTemplate.getForEntity(
                "/clientes/" + creado.getBody().getId(), Cliente.class);
        assertThat(obtenido.getBody().getEmail()).isEqualTo("eva.espacios@example.com");
    }
}
