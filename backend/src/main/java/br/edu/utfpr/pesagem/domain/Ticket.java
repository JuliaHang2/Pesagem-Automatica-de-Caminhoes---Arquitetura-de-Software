package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "tickets")
@Getter @Setter @NoArgsConstructor
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String codigo;
    @ManyToOne(optional = false) private Veiculo veiculo;
    @ManyToOne(optional = false) private Pedido pedido;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusTicket status = StatusTicket.EMITIDO;
    private boolean placaManual;
    @ManyToOne private Usuario emitidoPor;
    private LocalDateTime entradaEm;
    private LocalDateTime saidaEm;
    private LocalDateTime atualizadoEm;
    @Column(precision = 12, scale = 2) private BigDecimal pesoLiquidoKg;
    private boolean excecaoAutorizada;
    private String justificativaExcecao;
}
