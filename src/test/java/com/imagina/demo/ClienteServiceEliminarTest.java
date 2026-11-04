package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import com.imagina.demo.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitario de la baja lógica (quinto caso límite): se separa en su propio fichero
 * porque usa Mockito sin contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceEliminarTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void eliminarGuardaElClienteInactivoYNuncaBorraLaFila() {
        Cliente cliente = new Cliente("Cliente Ficticio", "cliente005@example.com");
        cliente.setId(5L);
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(cliente));

        clienteService.eliminar(5L);

        ArgumentCaptor<Cliente> guardado = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(guardado.capture());
        assertThat(guardado.getValue().getId()).isEqualTo(5L);
        assertThat(guardado.getValue().isActivo()).isFalse();
        verify(clienteRepository, never()).deleteById(any());
    }
}
