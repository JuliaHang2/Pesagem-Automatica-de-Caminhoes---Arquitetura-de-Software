package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCodigo(String codigo);
    boolean existsByVeiculoAndStatusNot(Veiculo veiculo, StatusTicket status);
    List<Ticket> findByStatus(StatusTicket status);
    List<Ticket> findByStatusNot(StatusTicket status);
    List<Ticket> findByVeiculoPlacaContainingIgnoreCaseOrderByEntradaEmDesc(String placa);
    Optional<Ticket> findFirstByStatusInOrderByAtualizadoEmDesc(Collection<StatusTicket> status);
}
