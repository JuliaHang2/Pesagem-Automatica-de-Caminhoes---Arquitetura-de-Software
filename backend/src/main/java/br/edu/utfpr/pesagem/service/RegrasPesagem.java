package br.edu.utfpr.pesagem.service;

import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Regras puras de pesagem, fáceis de testar sem banco. */
public final class RegrasPesagem {
    private RegrasPesagem() {}

    /** RN09 e RN10: bruto deve ser maior que a tara; líquido = bruto - tara. */
    public static BigDecimal calcularLiquido(BigDecimal bruto, BigDecimal tara) {
        if (bruto.compareTo(tara) <= 0) {
            throw new RegraNegocioException("RN09: o peso bruto deve ser maior que a tara; pesagem inválida.");
        }
        return bruto.subtract(tara);
    }

    /** RN11: verdadeiro se a diferença entre o líquido e o previsto passa da tolerância (em %). */
    public static boolean excedeTolerancia(BigDecimal liquido, BigDecimal previsto, BigDecimal toleranciaPercentual) {
        BigDecimal diferenca = liquido.subtract(previsto).abs();
        BigDecimal limite = previsto.multiply(toleranciaPercentual).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return diferenca.compareTo(limite) > 0;
    }

    public static String normalizarPlaca(String placa) {
        return placa == null ? "" : placa.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /** Aceita padrão antigo (ABC1234) e Mercosul (ABC1D23). */
    public static boolean placaValida(String placaNormalizada) {
        return placaNormalizada.matches("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");
    }
}
