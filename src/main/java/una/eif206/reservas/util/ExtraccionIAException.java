package una.eif206.reservas.util;

public class ExtraccionIAException extends Exception {
    public ExtraccionIAException(String mensaje) { super(mensaje); }
    public ExtraccionIAException(String mensaje, Throwable causa) { super(mensaje, causa); }
}