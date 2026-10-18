package una.eif206.reservas.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExtractorReservaIA {

    private final GeminiService geminiService;

    public ExtractorReservaIA() { this(new GeminiService()); }
    public ExtractorReservaIA(GeminiService geminiService) { this.geminiService = geminiService; }

    public ReservaExtraida extraer(String frase, List<String> categoriasDisponibles) throws ExtraccionIAException {
        if (frase == null || frase.isBlank()) {
            throw new ExtraccionIAException("Escriba una frase describiendo la reserva antes de usar la IA.");
        }

        String prompt = construirPrompt(frase, categoriasDisponibles);

        String respuestaTexto;
        try {
            respuestaTexto = geminiService.enviarMensaje(prompt);
        } catch (Exception e) {
            throw new ExtraccionIAException("No se pudo contactar al servicio de IA: " + e.getMessage(), e);
        }

        return interpretarRespuesta(respuestaTexto);
    }

    private String construirPrompt(String frase, List<String> categoriasDisponibles) {
        StringBuilder listaCategorias = new StringBuilder();
        for (String categoria : categoriasDisponibles) {
            listaCategorias.append("- ").append(categoria).append("\n");
        }

        return "Hoy es " + LocalDate.now() + ". A partir de la siguiente frase de un usuario que quiere "
                + "reservar recursos para una actividad, extrae los datos y responde UNICAMENTE con un "
                + "objeto JSON valido, sin explicaciones, sin marcado de codigo (nada de ```), con "
                + "exactamente estos campos:\n"
                + "{\n"
                + "  \"actividad\": \"nombre corto de la actividad\",\n"
                + "  \"fecha\": \"AAAA-MM-DD\",\n"
                + "  \"horaInicio\": \"HH:mm en formato 24 horas\",\n"
                + "  \"horaFin\": \"HH:mm en formato 24 horas\",\n"
                + "  \"categorias\": [\"lista de categorias solicitadas, usando EXACTAMENTE el texto de "
                + "las categorias disponibles listadas abajo que mejor correspondan\"]\n"
                + "}\n\n"
                + "Categorias disponibles:\n" + listaCategorias
                + "\nFrase del usuario: \"" + frase + "\"";
    }

    private ReservaExtraida interpretarRespuesta(String textoRespuesta) throws ExtraccionIAException {
        String textoLimpio = limpiarMarcadoMarkdown(textoRespuesta);

        JSONObject json;
        try {
            json = new JSONObject(textoLimpio);
        } catch (Exception e) {
            throw new ExtraccionIAException("La IA no devolvió un formato reconocible. Intente reformular la frase.");
        }

        ReservaExtraida resultado = new ReservaExtraida();
        resultado.setActividad(json.optString("actividad", ""));
        resultado.setFecha(json.optString("fecha", ""));
        resultado.setHoraInicio(json.optString("horaInicio", ""));
        resultado.setHoraFin(json.optString("horaFin", ""));

        List<String> categorias = new ArrayList<>();
        if (json.has("categorias")) {
            JSONArray arreglo = json.getJSONArray("categorias");
            for (int i = 0; i < arreglo.length(); i++) {
                categorias.add(arreglo.getString(i));
            }
        }
        resultado.setCategoriasDescripcion(categorias);

        return resultado;
    }

    private String limpiarMarcadoMarkdown(String texto) {
        String resultado = texto.trim();
        if (resultado.startsWith("```")) {
            int primerSalto = resultado.indexOf('\n');
            if (primerSalto != -1) {
                resultado = resultado.substring(primerSalto + 1);
            }
            int finBloque = resultado.lastIndexOf("```");
            if (finBloque != -1) {
                resultado = resultado.substring(0, finBloque);
            }
        }
        return resultado.trim();
    }
}