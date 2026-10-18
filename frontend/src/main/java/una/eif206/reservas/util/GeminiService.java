package una.eif206.reservas.util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {
    // La version de gemini debe ser la mas actual ya que la 2.5 esta bloqueada
    private static final String MODELO = "gemini-3.5-flash-lite";
    private static final String ENDPOINT_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    public String enviarMensaje(String textoUsuario)
            throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IOException(
                    "No se encontró la variable de entorno GEMINI_API_KEY. "
                            + "Configúrela en Run > Edit Configurations > Environment variables.");
        }

        String url = ENDPOINT_BASE + MODELO + ":generateContent";

        JSONObject parte = new JSONObject().put("text", textoUsuario);
        JSONObject contenido = new JSONObject()
                .put("parts", new JSONArray().put(parte));
        JSONObject cuerpo = new JSONObject()
                .put("contents", new JSONArray().put(contenido));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error de la API (HTTP "
                    + response.statusCode() + "): " + response.body());
        }

        return extraerTexto(response.body());
    }

    private String extraerTexto(String jsonRespuesta) {
        JSONObject raiz = new JSONObject(jsonRespuesta);
        JSONArray candidatos = raiz.getJSONArray("candidates");
        JSONObject primerCandidato = candidatos.getJSONObject(0);
        JSONObject contenido = primerCandidato.getJSONObject("content");
        JSONArray partes = contenido.getJSONArray("parts");
        return partes.getJSONObject(0).getString("text");
    }
}