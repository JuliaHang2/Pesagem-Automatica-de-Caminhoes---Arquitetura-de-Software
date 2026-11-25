package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "veiculos")
@Getter @Setter @NoArgsConstructor
public class Veiculo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String placa;
    @ManyToOne private Transportadora transportadora;
}
