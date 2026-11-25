package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.domain.Parametro;
import br.edu.utfpr.pesagem.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Service @RequiredArgsConstructor
public class ParametroService {
    public static final String TOLERANCIA = "TOLERANCIA_PERCENTUAL";
    public static final String TEMPO_MAX = "TEMPO_MAX_PERMANENCIA_MIN";
    public static final String CALIBRACAO = "CALIBRACAO_VALIDADE";

    private final ParametroRepository repo;
    private final AuditoriaService auditoria;

    public String texto(String chave, String padrao) {
        return repo.findById(chave).map(Parametro::getValor).orElse(padrao);
    }
    public BigDecimal decimal(String chave, String padrao) { return new BigDecimal(texto(chave, padrao)); }

    public Map<String, String> todos() {
        Map<String, String> m = new TreeMap<>();
        repo.findAll().forEach(p -> m.put(p.getChave(), p.getValor()));
        return m;
    }

    public void atualizar(Map<String, String> novos) {
        novos.forEach((k, v) -> {
            if (!k.equals(TOLERANCIA) && !k.equals(TEMPO_MAX) && !k.equals(CALIBRACAO)) return;
            String antigo = texto(k, "");
            repo.save(new Parametro(k, v));
            auditoria.registrar("PARAMETRO_ALTERADO", k + ": " + antigo + " -> " + v);
        });
    }
}
