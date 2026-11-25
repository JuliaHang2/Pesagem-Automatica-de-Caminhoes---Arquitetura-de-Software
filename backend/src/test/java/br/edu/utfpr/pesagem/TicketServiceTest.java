package br.edu.utfpr.pesagem;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import br.edu.utfpr.pesagem.repository.*;
import br.edu.utfpr.pesagem.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock TicketRepository tickets;
    @Mock VeiculoRepository veiculos;
    @Mock PedidoRepository pedidos;
    @Mock PesagemRepository pesagens;
    @Mock AuditoriaService auditoria;
    @Mock UsuarioAtual usuarioAtual;
    @InjectMocks TicketService service;

    @Test void semPedidoProgramadoNaoEmiteTicket_RN03() {
        when(pedidos.findFirstByPlacaProgramadaAndStatus("ABC1D23", StatusPedido.PROGRAMADO)).thenReturn(Optional.empty());
        var e = assertThrows(RegraNegocioException.class, () -> service.emitir("abc1d23", true));
        assertTrue(e.getMessage().contains("RN03"));
    }

    @Test void veiculoComTicketAbertoNaoEmiteOutro_RN04() {
        Veiculo v = new Veiculo(); v.setPlaca("ABC1D23");
        when(pedidos.findFirstByPlacaProgramadaAndStatus("ABC1D23", StatusPedido.PROGRAMADO)).thenReturn(Optional.of(new Pedido()));
        when(veiculos.findByPlaca("ABC1D23")).thenReturn(Optional.of(v));
        when(tickets.existsByVeiculoAndStatusNot(v, StatusTicket.ENCERRADO)).thenReturn(true);
        var e = assertThrows(RegraNegocioException.class, () -> service.emitir("ABC1D23", true));
        assertTrue(e.getMessage().contains("RN04"));
    }

    @Test void placaInvalidaEhRejeitada() {
        assertThrows(RegraNegocioException.class, () -> service.emitir("123", true));
    }
}
