package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "transportadoras")
@Getter @Setter @NoArgsConstructor
public class Transportadora {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nome;
    public Transportadora(String nome) { this.nome = nome; }
}
