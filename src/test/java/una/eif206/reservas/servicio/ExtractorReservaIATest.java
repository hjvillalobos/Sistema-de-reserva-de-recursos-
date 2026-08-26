package una.eif206.reservas.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExtractorReservaIATest {

    // GeminiService de prueba: no llama a la API real, devuelve un texto fijo.
    static class GeminiServiceFalso extends GeminiService {
        private final String respuestaSimulada;
        private final boolean lanzarError;

        GeminiServiceFalso(String respuestaSimulada) {
            this.respuestaSimulada = respuestaSimulada;
            this.lanzarError = false;
        }

        GeminiServiceFalso() {
            this.respuestaSimulada = null;
            this.lanzarError = true;
        }

        @Override
        public String enviarMensaje(String textoUsuario) throws IOException {
            if (lanzarError) {
                throw new IOException("Fallo simulado de red");
            }
            return respuestaSimulada;
        }
    }

    @Test
    void extraerConJsonLimpioFuncionaCorrectamente() throws ExtraccionIAException {
        String jsonSimulado = "{"
                + "\"actividad\":\"Reunion de trabajo\","
                + "\"fecha\":\"2026-08-30\","
                + "\"horaInicio\":\"08:00\","
                + "\"horaFin\":\"10:00\","
                + "\"categorias\":[\"Laptop windows\",\"Sala de Juntas\"]"
                + "}";

        ExtractorReservaIA extractor = new ExtractorReservaIA(new GeminiServiceFalso(jsonSimulado));
        ReservaExtraida resultado = extractor.extraer("necesito una reunion", List.of("Laptop windows", "Sala de Juntas"));

        assertEquals("Reunion de trabajo", resultado.getActividad());
        assertEquals("2026-08-30", resultado.getFecha());
        assertEquals("08:00", resultado.getHoraInicio());
        assertEquals("10:00", resultado.getHoraFin());
        assertEquals(2, resultado.getCategoriasDescripcion().size());
    }

    @Test
    void extraerConJsonEnvueltoEnMarkdownLoLimpiaCorrectamente() throws ExtraccionIAException {
        String jsonConMarkdown = "```json\n"
                + "{\"actividad\":\"Charla\",\"fecha\":\"2026-09-01\",\"horaInicio\":\"14:00\","
                + "\"horaFin\":\"15:00\",\"categorias\":[\"Sala de Juntas\"]}\n"
                + "```";

        ExtractorReservaIA extractor = new ExtractorReservaIA(new GeminiServiceFalso(jsonConMarkdown));
        ReservaExtraida resultado = extractor.extraer("charla mañana", List.of("Sala de Juntas"));

        assertEquals("Charla", resultado.getActividad());
        assertEquals(1, resultado.getCategoriasDescripcion().size());
    }

    @Test
    void extraerConFraseVaciaLanzaExcepcionSinLlamarALaApi() {
        ExtractorReservaIA extractor = new ExtractorReservaIA(new GeminiServiceFalso("no debería usarse"));
        assertThrows(ExtraccionIAException.class, () -> extractor.extraer("", List.of("Sala de Juntas")));
    }

    @Test
    void extraerConRespuestaNoJsonLanzaExcepcion() {
        ExtractorReservaIA extractor = new ExtractorReservaIA(new GeminiServiceFalso("esto no es json"));
        assertThrows(ExtraccionIAException.class, () -> extractor.extraer("frase valida", List.of("Sala de Juntas")));
    }

    @Test
    void extraerConFalloDeRedLanzaExcepcionEnvuelta() {
        ExtractorReservaIA extractor = new ExtractorReservaIA(new GeminiServiceFalso());
        assertThrows(ExtraccionIAException.class, () -> extractor.extraer("frase valida", List.of("Sala de Juntas")));
    }
}