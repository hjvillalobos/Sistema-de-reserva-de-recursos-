package una.eif206.reservas.datos;

import java.sql.SQLException;
import una.eif206.reservas.DTO.RolDTO;

public interface UsuariosAcceso {
    record Credencial(String id, String hash, RolDTO rol) { }
    Credencial buscar(String id) throws SQLException;
    boolean cambiarHash(String id, String anterior, String nuevo) throws SQLException;
}
