package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro imutável de pesagem (RN08). Correções criam novo registro e marcam o anterior como não vigente. */
@Entity @Table(name = "pesagens")
@Getter @Setter @NoArgsConstructor
public class Pesagem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Ticket ticket;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TipoPesagem tipo;
    @Column(precision = 12, scale = 2, nullable = false) private BigDecimal pesoKg;
    private LocalDateTime dataHora;
    private String usuario;
    private boolean vigente = true;
    private String justificativa;
    private Long substituiPesagemId;
}
