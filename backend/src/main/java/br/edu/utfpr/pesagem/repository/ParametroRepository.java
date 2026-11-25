package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametroRepository extends JpaRepository<Parametro, String> {}
