package una.eif206.reservas.protocolo;

import java.io.Serializable;

public record Peticion(long id, String servicio, String metodo, Object[] argumentos) implements Serializable { }
