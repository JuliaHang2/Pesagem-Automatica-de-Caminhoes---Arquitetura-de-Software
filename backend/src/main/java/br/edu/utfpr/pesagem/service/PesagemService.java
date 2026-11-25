package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import br.edu.utfpr.pesagem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class PesagemService {
    private final TicketService ticketService;
    private final TicketRepository tickets;
    private final PesagemRepository pesagens;
    private final PedidoRepository pedidos;
    private final ParametroService parametros;
    private final AuditoriaService auditoria;
    private final UsuarioAtual usuarioAtual;

    /** Registra tara ou peso bruto, conforme o estado do ticket (RN05 a RN11, RN16, RN20). */
    @Transactional
    public Ticket registrar(String codigo, BigDecimal pesoKg, boolean estabilizado, String placaLida) {
        Ticket t = ticketService.buscarPorCodigo(codigo);
        if (t.getStatus() == StatusTicket.ENCERRADO) {
            throw new RegraNegocioException("RN16: ticket já encerrado; não pode ser reutilizado.");
        }
        if (t.getStatus() == StatusTicket.AGUARDANDO_AUTORIZACAO) {
            throw new RegraNegocioException("Pesagem final já registrada; aguardando autorização do supervisor.");
        }
        verificarCalibracao();
        if (pesoKg == null || pesoKg.signum() <= 0) {
            throw new RegraNegocioException("Informe um peso válido.");
        }
        if (!estabilizado) {
            throw new RegraNegocioException("RN06: leitura de peso não estabilizada; aguarde e tente novamente.");
        }
        if (placaLida != null && !placaLida.isBlank()
                && !RegrasPesagem.normalizarPlaca(placaLida).equals(t.getVeiculo().getPlaca())) {
            auditoria.registrar("PESAGEM_BLOQUEADA", t.getCodigo() + " placa divergente: " + placaLida);
            throw new RegraNegocioException("RN07: placa da balança diverge da placa do ticket; pesagem bloqueada.");
        }

        LocalDateTime agora = LocalDateTime.now();
        if (t.getStatus() == StatusTicket.EMITIDO) {
            salvar(t, TipoPesagem.TARA, pesoKg, agora);
            t.setStatus(StatusTicket.TARA_REGISTRADA);
            auditoria.registrar("TARA_REGISTRADA", t.getCodigo() + " " + pesoKg + " kg");
        } else {
            Pesagem tara = pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.TARA)
                    .orElseThrow(() -> new RegraNegocioException("RN05: tara não registrada."));
            BigDecimal liquido = RegrasPesagem.calcularLiquido(pesoKg, tara.getPesoKg());
            salvar(t, TipoPesagem.BRUTO, pesoKg, agora);
            t.setPesoLiquidoKg(liquido);
            auditoria.registrar("BRUTO_REGISTRADO", t.getCodigo() + " bruto=" + pesoKg + " liquido=" + liquido);
            BigDecimal tolerancia = parametros.decimal(ParametroService.TOLERANCIA, "5");
            if (RegrasPesagem.excedeTolerancia(liquido, t.getPedido().getPesoPrevistoKg(), tolerancia)) {
                t.setStatus(StatusTicket.AGUARDANDO_AUTORIZACAO);
                auditoria.registrar("DIVERGENCIA_PESO", t.getCodigo() + " previsto=" + t.getPedido().getPesoPrevistoKg()
                        + " liquido=" + liquido + " tol=" + tolerancia + "%");
            } else {
                encerrar(t);
            }
        }
        t.setAtualizadoEm(agora);
        return tickets.save(t);
    }

    /** Libera o caminhão e encerra o ticket (RN16). No MVP a NF-e é simulada (liberação direta). */
    @Transactional
    public void encerrar(Ticket t) {
        LocalDateTime agora = LocalDateTime.now();
        t.setStatus(StatusTicket.ENCERRADO);
        t.setSaidaEm(agora);
        t.setAtualizadoEm(agora);
        Pedido p = t.getPedido();
        p.setStatus(StatusPedido.EXPEDIDO);
        pedidos.save(p);
        tickets.save(t);
        auditoria.registrar("TICKET_ENCERRADO", t.getCodigo() + " liberado");
    }

    private void salvar(Ticket t, TipoPesagem tipo, BigDecimal peso, LocalDateTime quando) {
        Pesagem p = new Pesagem();
        p.setTicket(t);
        p.setTipo(tipo);
        p.setPesoKg(peso);
        p.setDataHora(quando);
        p.setUsuario(usuarioAtual.login());
        pesagens.save(p);
    }

    /** RN20: calibração vencida bloqueia novas pesagens. */
    private void verificarCalibracao() {
        LocalDate validade = LocalDate.parse(parametros.texto(ParametroService.CALIBRACAO, "2099-12-31"));
        if (validade.isBefore(LocalDate.now())) {
            auditoria.registrar("CALIBRACAO_VENCIDA", "validade=" + validade);
            throw new RegraNegocioException("RN20: calibração da balança vencida; pesagens bloqueadas.");
        }
    }
}
