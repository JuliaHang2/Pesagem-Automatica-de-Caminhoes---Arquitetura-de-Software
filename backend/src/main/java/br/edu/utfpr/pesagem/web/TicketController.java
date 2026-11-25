package br.edu.utfpr.pesagem.web;

import br.edu.utfpr.pesagem.dto.Dtos.*;
import br.edu.utfpr.pesagem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final SupervisorService supervisorService;
    private final PesagemService pesagemService;
    private final BalancaSimulada balanca;
    private final br.edu.utfpr.pesagem.repository.PesagemRepository pesagens;

    @PostMapping("/tickets")
    public TicketDto emitir(@RequestBody EmitirTicketRequest r) {
        return ticketService.toDto(ticketService.emitir(r.placa(), r.manual()));
    }

    @GetMapping("/tickets")
    public List<TicketDto> historico(@RequestParam(defaultValue = "") String placa) {
        return ticketService.historico(placa);
    }

    @GetMapping("/tickets/{codigo}")
    public TicketDto buscar(@PathVariable String codigo) {
        return ticketService.toDto(ticketService.buscarPorCodigo(codigo));
    }

    @GetMapping("/tickets/{codigo}/pesagens")
    public List<PesagemDto> pesagensDoTicket(@PathVariable String codigo) {
        return pesagens.findByTicketOrderByDataHora(ticketService.buscarPorCodigo(codigo)).stream()
                .map(p -> new PesagemDto(p.getId(), p.getTipo().name(), p.getPesoKg(), p.getDataHora(),
                        p.getUsuario(), p.isVigente(), p.getJustificativa())).toList();
    }

    @PostMapping("/tickets/{id}/autorizar")
    public TicketDto autorizar(@PathVariable Long id, @RequestBody JustificativaRequest r) {
        return ticketService.toDto(supervisorService.autorizar(id, r.justificativa()));
    }

    @PostMapping("/pesagens")
    public TicketDto pesar(@RequestBody PesagemRequest r) {
        return ticketService.toDto(pesagemService.registrar(r.codigoTicket(), r.pesoKg(), r.estabilizado(), r.placaLida()));
    }

    @PostMapping("/pesagens/{id}/corrigir")
    public PesagemDto corrigir(@PathVariable Long id, @RequestBody CorrecaoRequest r) {
        var p = supervisorService.corrigir(id, r.novoPesoKg(), r.justificativa());
        return new PesagemDto(p.getId(), p.getTipo().name(), p.getPesoKg(), p.getDataHora(), p.getUsuario(), p.isVigente(), p.getJustificativa());
    }

    @GetMapping("/balanca/simular")
    public SimulacaoDto simular(@RequestParam String codigo, @RequestParam(defaultValue = "false") boolean divergente) {
        return balanca.ler(codigo, divergente);
    }
}
