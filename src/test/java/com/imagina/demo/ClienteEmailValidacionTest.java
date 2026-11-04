package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteEmailValidacionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void altaConEmailValidoSeGuarda() {
        ResponseEntity<Cliente> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Luis Prueba", "luis.prueba@example.com"), Cliente.class);

        assertThat(respuesta.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(respuesta.getBody().getId()).isNotNull();
    }

    @Test
    void altaConEmailSinArrobaDevuelve400YNoSeGuarda() {
        ResponseEntity<String> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Marta Prueba", "marta.example.com"), String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).contains("El email no tiene un formato válido");
        assertNoGuardado("marta.example.com");
    }

    @Test
    void altaConEspaciosDentroDelEmailDevuelve400YNoSeGuarda() {
        ResponseEntity<String> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Pedro Prueba", "pedro prueba@example.com"), String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNoGuardado("pedro prueba@example.com");
    }

    @Test
    void altaSinEmailDevuelve400() {
        ResponseEntity<String> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Sin Email", null), String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private void assertNoGuardado(String email) {
        assertThat(clienteRepository.findAll())
                .extracting(Cliente::getEmail)
                .doesNotContain(email);
    }
}
