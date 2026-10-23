package una.eif206.reservas.excepciones;

/** Errores de conexión o de operaciones cuyos contratos no declaran excepción comprobada. */
public class ServicioRemotoException extends RuntimeException {
    public ServicioRemotoException(String mensaje) { super(mensaje); }
    public ServicioRemotoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
