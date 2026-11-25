package br.edu.utfpr.pesagem.repository;

import br.edu.utfpr.pesagem.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
}
