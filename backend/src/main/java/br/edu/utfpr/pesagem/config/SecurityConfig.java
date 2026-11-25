package br.edu.utfpr.pesagem.config;

import br.edu.utfpr.pesagem.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** RN19 e RNF05: login individual, sessão com expiração e autorização por perfil em todas as rotas. */
@Configuration @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/**", "/api/painel").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tickets/*/autorizar", "/api/pesagens/*/corrigir").hasRole("SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/parametros").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/auditoria", "/api/dashboard", "/api/alertas").hasAnyRole("SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/tickets", "/api/pesagens").hasAnyRole("OPERADOR", "SUPERVISOR", "ADMINISTRADOR")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
