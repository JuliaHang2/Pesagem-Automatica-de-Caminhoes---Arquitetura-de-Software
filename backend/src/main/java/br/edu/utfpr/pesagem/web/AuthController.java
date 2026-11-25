package br.edu.utfpr.pesagem.web;

import br.edu.utfpr.pesagem.dto.Dtos.*;
import br.edu.utfpr.pesagem.repository.UsuarioRepository;
import br.edu.utfpr.pesagem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var u = usuarios.findByLogin(req.login()).filter(x -> x.isAtivo())
                .filter(x -> encoder.matches(req.senha() == null ? "" : req.senha(), x.getSenhaHash()));
        if (u.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha inválidos."));
        var x = u.get();
        return ResponseEntity.ok(new LoginResponse(jwt.gerar(x), x.getNome(), x.getPerfil().name()));
    }
}
