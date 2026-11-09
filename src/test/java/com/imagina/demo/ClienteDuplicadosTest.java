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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteDuplicadosTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        clienteRepository.deleteAll();
    }

    @Test
    void altaConEmailNuevoDevuelveElEstadoDeExitoActual() {
        ResponseEntity<String> respuesta = alta("Cliente Uno", "cliente001@example.com");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void segundaAltaConElMismoEmailDevuelve409() {
        alta("Cliente Dos", "cliente002@example.com");

        ResponseEntity<String> respuesta = alta("Otro Cliente", "cliente002@example.com");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(respuesta.getBody()).doesNotContain("Exception").doesNotContain("trace");
        assertThat(clienteRepository.count()).isEqualTo(1);
    }

    @Test
    void segundaAltaConElMismoEmailEnOtrasMayusculasYConEspaciosDevuelve409() {
        alta("Cliente Tres", "cliente003@example.com");

        ResponseEntity<String> respuesta = alta("Otro Cliente", "  Cliente003@EXAMPLE.com  ");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(clienteRepository.count()).isEqualTo(1);
    }

    @Test
    void altaConElEmailDeUnClienteDadoDeBajaSePermite() {
        ResponseEntity<Cliente> creado = restTemplate.postForEntity(
                "/clientes", new Cliente("Cliente Cuatro", "cliente004@example.com"), Cliente.class);
        restTemplate.exchange("/clientes/" + creado.getBody().getId(), HttpMethod.DELETE, null, Void.class);

        ResponseEntity<String> respuesta = alta("Cliente Cuatro", "cliente004@example.com");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void dosClientesConElMismoNombreYDistintoEmailSePermiten() {
        ResponseEntity<String> primero = alta("José García", "cliente005@example.com");
        ResponseEntity<String> segundo = alta("José García", "cliente006@example.com");

        assertThat(primero.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(segundo.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> alta(String nombre, String email) {
        return restTemplate.postForEntity("/clientes", new Cliente(nombre, email), String.class);
    }
}
