package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findTop100ByOrderByDataHoraDesc();
}
