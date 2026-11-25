package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "auditoria")
@Getter @Setter @NoArgsConstructor
public class Auditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private LocalDateTime dataHora;
    private String usuario;
    private String operacao;
    @Column(length = 500) private String detalhe;
}
