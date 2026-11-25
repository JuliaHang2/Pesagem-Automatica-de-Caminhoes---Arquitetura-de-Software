package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
