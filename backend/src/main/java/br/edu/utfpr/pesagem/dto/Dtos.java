package br.edu.utfpr.pesagem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Dtos {
    private Dtos() {}
    public record LoginRequest(String login, String senha) {}
    public record LoginResponse(String token, String nome, String perfil) {}
    public record EmitirTicketRequest(String placa, boolean manual) {}
    public record PesagemRequest(String codigoTicket, BigDecimal pesoKg, boolean estabilizado, String placaLida) {}
    public record JustificativaRequest(String justificativa) {}
    public record CorrecaoRequest(BigDecimal novoPesoKg, String justificativa) {}
    public record SimulacaoDto(String tipo, BigDecimal pesoKg, boolean estabilizado) {}
    public record TicketDto(Long id, String codigo, String placa, String status, String cliente, String transportadora,
                            String produto, BigDecimal pesoPrevistoKg, BigDecimal taraKg, BigDecimal brutoKg,
                            BigDecimal pesoLiquidoKg, LocalDateTime entradaEm, LocalDateTime saidaEm,
                            boolean placaManual, boolean excecaoAutorizada) {}
    public record PesagemDto(Long id, String tipo, BigDecimal pesoKg, LocalDateTime dataHora, String usuario,
                             boolean vigente, String justificativa) {}
    public record PedidoDto(Long id, String cliente, String transportadora, String produto,
                            BigDecimal pesoPrevistoKg, String placaProgramada, String status) {}
    public record PainelDto(String mensagem, String placa, BigDecimal pesoKg) {}
    public record AlertaDto(String codigo, String placa, long minutos) {}
    public record DashboardDto(long caminhoesAtendidos, long ticketsAbertos, double tempoMedioMin, BigDecimal volumeTotalKg) {}
}
