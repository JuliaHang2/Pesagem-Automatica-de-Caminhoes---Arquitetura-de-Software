package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.dto.Dtos.*;
import br.edu.utfpr.pesagem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service @RequiredArgsConstructor
public class RelatorioService {
    private final TicketRepository tickets;
    private final ParametroService parametros;

    public DashboardDto dashboard() {
        List<Ticket> encerrados = tickets.findByStatus(StatusTicket.ENCERRADO);
        double media = encerrados.stream().filter(t -> t.getSaidaEm() != null)
                .mapToLong(t -> Duration.between(t.getEntradaEm(), t.getSaidaEm()).toMinutes()).average().orElse(0);
        BigDecimal volume = encerrados.stream().map(Ticket::getPesoLiquidoKg)
                .filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        long abertos = tickets.findByStatusNot(StatusTicket.ENCERRADO).size();
        return new DashboardDto(encerrados.size(), abertos, Math.round(media * 10) / 10.0, volume);
    }

    /** RN17: tickets abertos acima do tempo máximo de permanência. */
    public List<AlertaDto> alertas() {
        long max = parametros.decimal(ParametroService.TEMPO_MAX, "120").longValue();
        LocalDateTime agora = LocalDateTime.now();
        return tickets.findByStatusNot(StatusTicket.ENCERRADO).stream()
                .map(t -> new AlertaDto(t.getCodigo(), t.getVeiculo().getPlaca(),
                        Duration.between(t.getEntradaEm(), agora).toMinutes()))
                .filter(a -> a.minutos() > max).toList();
    }

    public PainelDto painel() {
        return tickets.findFirstByStatusInOrderByAtualizadoEmDesc(Set.of(StatusTicket.ENCERRADO, StatusTicket.AGUARDANDO_AUTORIZACAO))
                .map(t -> t.getStatus() == StatusTicket.ENCERRADO
                        ? new PainelDto("LIBERADO", t.getVeiculo().getPlaca(), t.getPesoLiquidoKg())
                        : new PainelDto("AGUARDE - SUPERVISOR", t.getVeiculo().getPlaca(), t.getPesoLiquidoKg()))
                .orElse(new PainelDto("AGUARDANDO VEÍCULO", "", null));
    }
}
