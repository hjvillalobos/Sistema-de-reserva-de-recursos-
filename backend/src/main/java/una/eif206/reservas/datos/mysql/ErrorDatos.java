package una.eif206.reservas.datos.mysql;

import java.sql.SQLException;

public final class ErrorDatos extends RuntimeException {
    public ErrorDatos(String mensaje) { super(mensaje); }
    public ErrorDatos(SQLException causa) {
        super(switch (causa.getErrorCode()) {
            case 1062 -> "Ya existe un registro con ese identificador.";
            case 1451 -> "No puede eliminar un registro asociado a otros datos o reservas.";
            case 1452 -> "La categoría, el funcionario o el recurso indicado no existe.";
            case 1205, 1213 -> "Otra operación está usando estos datos. Vuelva a intentarlo.";
            default -> "No se pudo completar la operación en MySQL. Revise la conexión del servidor.";
        }, causa);
    }
}
