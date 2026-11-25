package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.Pedido;
import br.edu.utfpr.pesagem.domain.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatus(StatusPedido status);
    Optional<Pedido> findFirstByPlacaProgramadaAndStatus(String placa, StatusPedido status);
}
