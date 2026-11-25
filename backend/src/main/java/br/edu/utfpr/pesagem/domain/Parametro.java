package br.edu.utfpr.pesagem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "parametros")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Parametro {
    @Id private String chave;
    private String valor;
}
