package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientePaginacionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        clienteRepository.deleteAll();
    }

    @Test
    void sinParametrosDevuelveLaPrimeraPaginaDe20() {
        crearClientes(45);

        assertThat(idsDe("/clientes")).hasSize(20);
    }

    @Test
    void laUltimaPaginaEstaIncompletaYLaSiguienteVacia() {
        crearClientes(45);

        assertThat(idsDe("/clientes?page=2")).hasSize(5);
        assertThat(idsDe("/clientes?page=3")).isEmpty();
    }

    @Test
    void elTamanoDePaginaSeLimitaA100() {
        crearClientes(105);

        assertThat(idsDe("/clientes?size=1000")).hasSize(100);
    }

    @Test
    void recorrerTodasLasPaginasDaUnOrdenEstablePorId() {
        List<Long> creados = crearClientes(25);

        List<Long> recorridos = recorrerPaginas(10);

        assertThat(recorridos).doesNotHaveDuplicates();
        assertThat(recorridos).isSorted();
        assertThat(recorridos).containsExactlyElementsOf(creados.stream().sorted().toList());
    }

    @Test
    void losClientesDadosDeBajaNoAparecenEnNingunaPagina() {
        List<Long> creados = crearClientes(30);
        List<Long> dadosDeBaja = new ArrayList<>();
        for (int i = 0; i < creados.size(); i += 3) {
            Long id = creados.get(i);
            restTemplate.exchange("/clientes/" + id, HttpMethod.DELETE, null, Void.class);
            dadosDeBaja.add(id);
        }

        List<Long> recorridos = recorrerPaginas(10);

        assertThat(recorridos).doesNotContainAnyElementsOf(dadosDeBaja);
        assertThat(recorridos).hasSize(creados.size() - dadosDeBaja.size());
    }

    private List<Long> crearClientes(int n) {
        List<Cliente> clientes = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            clientes.add(new Cliente("Cliente " + i, String.format("cliente%03d@example.com", i)));
        }
        return clienteRepository.saveAll(clientes).stream().map(Cliente::getId).toList();
    }

    private List<Long> recorrerPaginas(int size) {
        List<Long> ids = new ArrayList<>();
        for (int page = 0; page < 100; page++) {
            List<Long> pagina = idsDe("/clientes?size=" + size + "&page=" + page);
            if (pagina.isEmpty()) {
                break;
            }
            ids.addAll(pagina);
        }
        return ids;
    }

    private List<Long> idsDe(String url) {
        ResponseEntity<String> respuesta = restTemplate.getForEntity(url, String.class);
        assertThat(respuesta.getStatusCode().is2xxSuccessful()).isTrue();
        List<Number> ids = JsonPath.read(respuesta.getBody(), "$.content[*].id");
        return ids.stream().map(Number::longValue).toList();
    }
}
