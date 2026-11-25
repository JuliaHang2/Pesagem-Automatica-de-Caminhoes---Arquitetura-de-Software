package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nome;
    @Column(unique = true, nullable = false) private String login;
    @Column(nullable = false) private String senhaHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Perfil perfil;
    private boolean ativo = true;
}
