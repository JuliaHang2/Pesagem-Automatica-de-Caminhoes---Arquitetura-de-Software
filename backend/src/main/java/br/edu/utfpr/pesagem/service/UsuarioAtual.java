package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.Usuario;
import br.edu.utfpr.pesagem.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class UsuarioAtual {
    private final UsuarioRepository usuarios;

    public String login() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return (a == null || a.getName() == null || "anonymousUser".equals(a.getName())) ? "sistema" : a.getName();
    }
    public Optional<Usuario> usuario() { return usuarios.findByLogin(login()); }
}
