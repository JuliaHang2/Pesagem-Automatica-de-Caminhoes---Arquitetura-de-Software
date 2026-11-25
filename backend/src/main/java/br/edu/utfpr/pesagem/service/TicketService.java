package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.dto.Dtos.TicketDto;
import br.edu.utfpr.pesagem.exception.NaoEncontradoException;
import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import br.edu.utfpr.pesagem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class TicketService {
    private final TicketRepository tickets;
    private final VeiculoRepository veiculos;
    private final PedidoRepository pedidos;
    private final PesagemRepository pesagens;
    private final AuditoriaService auditoria;
    private final UsuarioAtual usuarioAtual;

    /** Emite o ticket de entrada (RN01 a RN04). */
    @Transactional
    public Ticket emitir(String placaInformada, boolean manual) {
        String placa = RegrasPesagem.normalizarPlaca(placaInformada);
        if (!RegrasPesagem.placaValida(placa)) {
            throw new RegraNegocioException("Placa inválida. Use o formato ABC1D23 ou ABC1234.");
        }
        Pedido pedido = pedidos.findFirstByPlacaProgramadaAndStatus(placa, StatusPedido.PROGRAMADO)
                .orElseThrow(() -> new RegraNegocioException(
                        "RN03: veículo sem carregamento programado. Encaminhe à portaria."));
        Veiculo veiculo = veiculos.findByPlaca(placa).orElseGet(() -> {
            Veiculo v = new Veiculo();
            v.setPlaca(placa);
            v.setTransportadora(pedido.getTransportadora());
            return veiculos.save(v);
        });
        if (tickets.existsByVeiculoAndStatusNot(veiculo, StatusTicket.ENCERRADO)) {
            throw new RegraNegocioException("RN04: o veículo já possui um ticket em aberto.");
        }
        LocalDateTime agora = LocalDateTime.now();
        Ticket t = new Ticket();
        t.setCodigo("TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        t.setVeiculo(veiculo);
        t.setPedido(pedido);
        t.setPlacaManual(manual);
        t.setEmitidoPor(usuarioAtual.usuario().orElse(null));
        t.setEntradaEm(agora);
        t.setAtualizadoEm(agora);
        t = tickets.save(t);
        auditoria.registrar("TICKET_EMITIDO", t.getCodigo() + " placa=" + placa + (manual ? " (digitada)" : ""));
        return t;
    }

    public Ticket buscarPorCodigo(String codigo) {
        return tickets.findByCodigo(codigo).orElseThrow(() -> new NaoEncontradoException("Ticket não encontrado."));
    }

    public TicketDto toDto(Ticket t) {
        var tara = pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.TARA).map(Pesagem::getPesoKg).orElse(null);
        var bruto = pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.BRUTO).map(Pesagem::getPesoKg).orElse(null);
        Pedido p = t.getPedido();
        return new TicketDto(t.getId(), t.getCodigo(), t.getVeiculo().getPlaca(), t.getStatus().name(),
                p.getCliente() != null ? p.getCliente().getNome() : null,
                p.getTransportadora() != null ? p.getTransportadora().getNome() : null,
                p.getProduto(), p.getPesoPrevistoKg(), tara, bruto, t.getPesoLiquidoKg(),
                t.getEntradaEm(), t.getSaidaEm(), t.isPlacaManual(), t.isExcecaoAutorizada());
    }

    public List<TicketDto> historico(String placa) {
        return tickets.findByVeiculoPlacaContainingIgnoreCaseOrderByEntradaEmDesc(RegrasPesagem.normalizarPlaca(placa))
                .stream().map(this::toDto).toList();
    }
}
