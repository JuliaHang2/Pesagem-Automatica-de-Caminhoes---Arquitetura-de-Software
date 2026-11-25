package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Pedido de carregamento (no MVP, vem de um ERP simulado). */
@Entity @Table(name = "pedidos")
@Getter @Setter @NoArgsConstructor
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne private Cliente cliente;
    @ManyToOne private Transportadora transportadora;
    private String produto;
    @Column(precision = 12, scale = 2) private BigDecimal pesoPrevistoKg;
    private String placaProgramada;
    @Enumerated(EnumType.STRING) private StatusPedido status = StatusPedido.PROGRAMADO;
}
