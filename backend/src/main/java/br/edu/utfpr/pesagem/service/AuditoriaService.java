package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.Auditoria;
import br.edu.utfpr.pesagem.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaRepository repo;
    private final UsuarioAtual usuarioAtual;

    /** Transação própria: o log permanece mesmo se a operação principal for revertida (RN18). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String operacao, String detalhe) {
        Auditoria a = new Auditoria();
        a.setDataHora(LocalDateTime.now());
        a.setUsuario(usuarioAtual.login());
        a.setOperacao(operacao);
        a.setDetalhe(detalhe != null && detalhe.length() > 500 ? detalhe.substring(0, 500) : detalhe);
        repo.save(a);
    }
}
