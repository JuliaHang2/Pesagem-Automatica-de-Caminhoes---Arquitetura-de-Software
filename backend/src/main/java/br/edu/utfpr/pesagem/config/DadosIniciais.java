package br.edu.utfpr.pesagem.config;

import br.edu.utfpr.pesagem.domain.*;
import br.edu.utfpr.pesagem.repository.*;
import br.edu.utfpr.pesagem.service.ParametroService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Carga inicial para demonstração: usuários, ERP simulado (pedidos) e parâmetros. */
@Component @RequiredArgsConstructor
public class DadosIniciais implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final TransportadoraRepository transportadoras;
    private final ClienteRepository clientes;
    private final PedidoRepository pedidos;
    private final ParametroRepository parametros;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;
        usuario("Operador Portaria", "operador", "operador123", Perfil.OPERADOR);
        usuario("Supervisor Logística", "supervisor", "supervisor123", Perfil.SUPERVISOR);
        usuario("Setor Fiscal", "fiscal", "fiscal123", Perfil.FISCAL);
        usuario("Administrador", "admin", "admin123", Perfil.ADMINISTRADOR);

        Transportadora t1 = transportadoras.save(new Transportadora("TransSul Logística"));
        Transportadora t2 = transportadoras.save(new Transportadora("Frio Express"));
        Cliente c1 = clientes.save(new Cliente("Supermercados Horizonte"));
        Cliente c2 = clientes.save(new Cliente("Distribuidora Boa Mesa"));
        pedido(c1, t1, "Frango congelado", "18000", "ABC1D23");
        pedido(c2, t2, "Cortes suínos", "22000", "EFG4H56");
        pedido(c1, t2, "Frango resfriado", "15000", "IJK7L89");

        parametros.save(new Parametro(ParametroService.TOLERANCIA, "5"));
        parametros.save(new Parametro(ParametroService.TEMPO_MAX, "120"));
        parametros.save(new Parametro(ParametroService.CALIBRACAO, LocalDate.now().plusMonths(6).toString()));
    }

    private void usuario(String nome, String login, String senha, Perfil perfil) {
        Usuario u = new Usuario();
        u.setNome(nome); u.setLogin(login); u.setSenhaHash(encoder.encode(senha)); u.setPerfil(perfil);
        usuarios.save(u);
    }

    private void pedido(Cliente c, Transportadora t, String produto, String peso, String placa) {
        Pedido p = new Pedido();
        p.setCliente(c); p.setTransportadora(t); p.setProduto(produto);
        p.setPesoPrevistoKg(new BigDecimal(peso)); p.setPlacaProgramada(placa);
        pedidos.save(p);
    }
}
