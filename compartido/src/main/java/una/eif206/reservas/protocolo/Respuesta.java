package una.eif206.reservas.protocolo;

import java.io.Serializable;

public record Respuesta(long id, Object resultado, String error) implements Serializable { }
