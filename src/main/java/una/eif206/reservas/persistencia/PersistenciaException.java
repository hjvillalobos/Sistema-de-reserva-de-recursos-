// persistencia/PersistenciaException.java
package una.eif206.reservas.persistencia;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}