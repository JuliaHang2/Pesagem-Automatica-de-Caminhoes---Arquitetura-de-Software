package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
    List<Pesagem> findByTicketOrderByDataHora(Ticket ticket);
    Optional<Pesagem> findFirstByTicketAndTipoAndVigenteTrue(Ticket ticket, TipoPesagem tipo);
}
