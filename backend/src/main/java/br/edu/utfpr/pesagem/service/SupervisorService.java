package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.exception.NaoEncontradoException;
import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import br.edu.utfpr.pesagem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class SupervisorService {
    private final TicketRepository tickets;
    private final PesagemRepository pesagens;
    private final PesagemService pesagemService;
    private final AuditoriaService auditoria;
    private final UsuarioAtual usuarioAtual;

    /** RN11: libera peso fora da tolerância mediante justificativa. */
    @Transactional
    public Ticket autorizar(Long ticketId, String justificativa) {
        exigirJustificativa(justificativa);
        Ticket t = tickets.findById(ticketId).orElseThrow(() -> new NaoEncontradoException("Ticket não encontrado."));
        if (t.getStatus() != StatusTicket.AGUARDANDO_AUTORIZACAO) {
            throw new RegraNegocioException("O ticket não está aguardando autorização.");
        }
        t.setExcecaoAutorizada(true);
        t.setJustificativaExcecao(justificativa);
        auditoria.registrar("EXCECAO_AUTORIZADA", t.getCodigo() + " : " + justificativa);
        pesagemService.encerrar(t);
        return t;
    }

    /** RN08: correção cria novo registro vinculado ao original, que é preservado. */
    @Transactional
    public Pesagem corrigir(Long pesagemId, BigDecimal novoPeso, String justificativa) {
        exigirJustificativa(justificativa);
        Pesagem antiga = pesagens.findById(pesagemId).orElseThrow(() -> new NaoEncontradoException("Pesagem não encontrada."));
        if (!antiga.isVigente()) throw new RegraNegocioException("Esta pesagem já foi corrigida.");
        Ticket t = antiga.getTicket();
        BigDecimal tara = antiga.getTipo() == TipoPesagem.TARA ? novoPeso
                : pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.TARA).map(Pesagem::getPesoKg).orElse(null);
        BigDecimal bruto = antiga.getTipo() == TipoPesagem.BRUTO ? novoPeso
                : pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.BRUTO).map(Pesagem::getPesoKg).orElse(null);
        BigDecimal liquido = null;
        if (tara != null && bruto != null) liquido = RegrasPesagem.calcularLiquido(bruto, tara);

        antiga.setVigente(false);
        pesagens.save(antiga);
        Pesagem nova = new Pesagem();
        nova.setTicket(t);
        nova.setTipo(antiga.getTipo());
        nova.setPesoKg(novoPeso);
        nova.setDataHora(LocalDateTime.now());
        nova.setUsuario(usuarioAtual.login());
        nova.setJustificativa(justificativa);
        nova.setSubstituiPesagemId(antiga.getId());
        nova = pesagens.save(nova);
        if (liquido != null) { t.setPesoLiquidoKg(liquido); tickets.save(t); }
        auditoria.registrar("PESAGEM_CORRIGIDA", t.getCodigo() + " " + antiga.getTipo() + " " + antiga.getPesoKg()
                + " -> " + novoPeso + " : " + justificativa);
        return nova;
    }

    private void exigirJustificativa(String j) {
        if (j == null || j.isBlank()) throw new RegraNegocioException("Justificativa obrigatória.");
    }
}
