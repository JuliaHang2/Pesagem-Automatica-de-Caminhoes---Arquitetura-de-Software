package br.edu.utfpr.pesagem;

import br.edu.utfpr.pesagem.exception.RegraNegocioException;
import br.edu.utfpr.pesagem.service.RegrasPesagem;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class RegrasPesagemTest {
    @Test void liquidoEhBrutoMenosTara() {
        assertEquals(new BigDecimal("18000"), RegrasPesagem.calcularLiquido(new BigDecimal("33000"), new BigDecimal("15000")));
    }
    @Test void brutoMenorOuIgualATaraEInvalido() {
        assertThrows(RegraNegocioException.class, () -> RegrasPesagem.calcularLiquido(new BigDecimal("15000"), new BigDecimal("15000")));
        assertThrows(RegraNegocioException.class, () -> RegrasPesagem.calcularLiquido(new BigDecimal("14000"), new BigDecimal("15000")));
    }
    @Test void toleranciaDentroEFora() {
        BigDecimal previsto = new BigDecimal("20000");
        assertFalse(RegrasPesagem.excedeTolerancia(new BigDecimal("20900"), previsto, new BigDecimal("5")));
        assertTrue(RegrasPesagem.excedeTolerancia(new BigDecimal("21100"), previsto, new BigDecimal("5")));
        assertTrue(RegrasPesagem.excedeTolerancia(new BigDecimal("18900"), previsto, new BigDecimal("5")));
    }
    @Test void placaNormalizadaEValidada() {
        assertEquals("ABC1D23", RegrasPesagem.normalizarPlaca("abc-1d23"));
        assertTrue(RegrasPesagem.placaValida("ABC1D23"));
        assertTrue(RegrasPesagem.placaValida("ABC1234"));
        assertFalse(RegrasPesagem.placaValida("AB123"));
    }
}
