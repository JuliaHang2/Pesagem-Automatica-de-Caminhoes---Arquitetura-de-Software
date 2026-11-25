package br.edu.utfpr.pesagem.web;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.dto.Dtos.*;
import br.edu.utfpr.pesagem.repository.*;
import br.edu.utfpr.pesagem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ConsultaController {
    private final PedidoRepository pedidos;
    private final TicketRepository tickets;
    private final TicketService ticketService;
    private final RelatorioService relatorios;
    private final ParametroService parametros;
    private final AuditoriaRepository auditoria;

    @GetMapping("/pedidos")
    public List<PedidoDto> pedidos(@RequestParam(defaultValue = "PROGRAMADO") StatusPedido status) {
        return pedidos.findByStatus(status).stream().map(p -> new PedidoDto(p.getId(),
                p.getCliente().getNome(), p.getTransportadora().getNome(), p.getProduto(),
                p.getPesoPrevistoKg(), p.getPlacaProgramada(), p.getStatus().name())).toList();
    }

    /** Caminhões com tara registrada, aptos a carregar (RN05). */
    @GetMapping("/expedicao")
    public List<TicketDto> expedicao() {
        return tickets.findByStatus(StatusTicket.TARA_REGISTRADA).stream().map(ticketService::toDto).toList();
    }

    @GetMapping("/painel")
    public PainelDto painel() { return relatorios.painel(); }

    @GetMapping("/dashboard")
    public DashboardDto dashboard() { return relatorios.dashboard(); }

    @GetMapping("/alertas")
    public List<AlertaDto> alertas() { return relatorios.alertas(); }

    @GetMapping("/auditoria")
    public List<Auditoria> auditoria() { return auditoria.findTop100ByOrderByDataHoraDesc(); }

    @GetMapping("/parametros")
    public Map<String, String> parametros() { return parametros.todos(); }

    @PutMapping("/parametros")
    public Map<String, String> atualizar(@RequestBody Map<String, String> novos) {
        parametros.atualizar(novos);
        return parametros.todos();
    }
}
