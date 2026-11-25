package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "clientes")
@Getter @Setter @NoArgsConstructor
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nome;
    public Cliente(String nome) { this.nome = nome; }
}
