package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

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

        // Los más recientes primero: si siguiera activo, estaría en esta página.
        ResponseEntity<String> listado = restTemplate.getForEntity(
                "/clientes?sort=id,desc&size=100", String.class);
        List<Number> ids = JsonPath.read(listado.getBody(), "$.content[*].id");
        assertThat(ids).extracting(Number::longValue).doesNotContain(id);
    }
}
