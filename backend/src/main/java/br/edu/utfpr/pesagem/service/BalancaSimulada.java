package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.dto.Dtos.SimulacaoDto;
import br.edu.utfpr.pesagem.repository.PesagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/** Adaptador de dispositivo de campo simulado (MVP). Substituível por integração real. */
@Component @RequiredArgsConstructor
public class BalancaSimulada {
    private final TicketService ticketService;
    private final PesagemRepository pesagens;

    public SimulacaoDto ler(String codigo, boolean divergente) {
        Ticket t = ticketService.buscarPorCodigo(codigo);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (t.getStatus() == StatusTicket.EMITIDO) {
            return new SimulacaoDto("TARA", arredondar(BigDecimal.valueOf(r.nextDouble(13000, 17000))), true);
        }
        BigDecimal tara = pesagens.findFirstByTicketAndTipoAndVigenteTrue(t, TipoPesagem.TARA)
                .map(Pesagem::getPesoKg).orElse(BigDecimal.valueOf(15000));
        double fator = divergente ? 1.15 : 1 + r.nextDouble(-0.03, 0.03);
        BigDecimal liquido = t.getPedido().getPesoPrevistoKg().multiply(BigDecimal.valueOf(fator));
        return new SimulacaoDto("BRUTO", arredondar(tara.add(liquido)), true);
    }

    private BigDecimal arredondar(BigDecimal v) {
        return v.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP).multiply(BigDecimal.TEN).setScale(2);
    }
}
