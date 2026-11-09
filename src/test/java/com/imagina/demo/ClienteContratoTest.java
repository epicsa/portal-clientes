package com.imagina.demo;

import com.imagina.demo.repository.ClienteRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteContratoTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        clienteRepository.deleteAll();
    }

    @Test
    void altaValidaNoDevuelveActivo() {
        ResponseEntity<String> respuesta = alta(cuerpo("Cliente Uno", "cliente001@example.com"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> json = JsonPath.read(respuesta.getBody(), "$");
        assertThat(json).containsKeys("id", "nombre", "email").doesNotContainKey("activo");
    }

    @Test
    void obtenerPorIdNoDevuelveActivo() {
        Number id = idDe(alta(cuerpo("Cliente Dos", "cliente002@example.com")));

        ResponseEntity<String> respuesta = restTemplate.getForEntity("/clientes/" + id, String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> json = JsonPath.read(respuesta.getBody(), "$");
        assertThat(json).containsKeys("id", "nombre", "email").doesNotContainKey("activo");
    }

    @Test
    void altaConActivoFalseCreaUnClienteActivo() {
        Map<String, Object> cuerpo = cuerpo("Cliente Tres", "cliente003@example.com");
        cuerpo.put("activo", false);

        Number id = idDe(alta(cuerpo));

        ResponseEntity<String> obtenido = restTemplate.getForEntity("/clientes/" + id, String.class);
        assertThat(obtenido.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void altaConElIdDeOtroClienteCreaUnoNuevoYNoModificaElOriginal() {
        Number idOriginal = idDe(alta(cuerpo("Cliente Original", "cliente004@example.com")));
        Map<String, Object> cuerpo = cuerpo("Cliente Intruso", "cliente005@example.com");
        cuerpo.put("id", idOriginal);

        Number idNuevo = idDe(alta(cuerpo));

        assertThat(idNuevo.longValue()).isNotEqualTo(idOriginal.longValue());
        ResponseEntity<String> original = restTemplate.getForEntity("/clientes/" + idOriginal, String.class);
        assertThat(original.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) JsonPath.read(original.getBody(), "$.nombre")).isEqualTo("Cliente Original");
        assertThat((String) JsonPath.read(original.getBody(), "$.email")).isEqualTo("cliente004@example.com");
    }

    @Test
    void elListadoDevuelveContentYPageSinPageableNiActivo() {
        alta(cuerpo("Cliente Seis", "cliente006@example.com"));

        ResponseEntity<String> respuesta = restTemplate.getForEntity("/clientes", String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> json = JsonPath.read(respuesta.getBody(), "$");
        assertThat(json).containsKey("content").containsKey("page").doesNotContainKey("pageable");
        Map<String, Object> pagina = JsonPath.read(respuesta.getBody(), "$.page");
        assertThat(pagina).containsKey("totalElements");
        Map<String, Object> primero = JsonPath.read(respuesta.getBody(), "$.content[0]");
        assertThat(primero).doesNotContainKey("activo");
    }

    private Map<String, Object> cuerpo(String nombre, String email) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("nombre", nombre);
        cuerpo.put("email", email);
        return cuerpo;
    }

    private ResponseEntity<String> alta(Map<String, Object> cuerpo) {
        return restTemplate.postForEntity("/clientes", cuerpo, String.class);
    }

    private Number idDe(ResponseEntity<String> respuesta) {
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        return JsonPath.read(respuesta.getBody(), "$.id");
    }
}
