package una.eif206.reservas.datos;

import java.sql.SQLException;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.configuracion.ConexionBD;

public final class UsuarioMySqlDatos implements UsuariosAcceso {
    @Override
    public Credencial buscar(String id) throws SQLException {
        try (var conexion = ConexionBD.abrir();
             var consulta = conexion.prepareStatement("SELECT id, clave_hash, rol FROM usuarios WHERE id = ?")) {
            consulta.setString(1, id);
            try (var resultado = consulta.executeQuery()) {
                if (!resultado.next()) return null;
                try {
                    return new Credencial(resultado.getString("id"), resultado.getString("clave_hash"),
                            RolDTO.valueOf(resultado.getString("rol")));
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Rol de usuario inválido.", e);
                }
            }
        }
    }

    @Override
    public boolean cambiarHash(String id, String anterior, String nuevo) throws SQLException {
        // Comparación exacta: evita sobrescribir otro cambio concurrente de contraseña.
        try (var conexion = ConexionBD.abrir();
             var consulta = conexion.prepareStatement(
                     "UPDATE usuarios SET clave_hash = ? WHERE id = ? AND CAST(clave_hash AS BINARY) = CAST(? AS BINARY)")) {
            consulta.setString(1, nuevo);
            consulta.setString(2, id);
            consulta.setString(3, anterior);
            return consulta.executeUpdate() == 1;
        }
    }

    public void crearAdministrador(String id, String hash) throws SQLException {
        try (var conexion = ConexionBD.abrir();
             var consulta = conexion.prepareStatement(
                     "INSERT INTO usuarios (id, clave_hash, rol) VALUES (?, ?, 'ADMINISTRADOR')")) {
            consulta.setString(1, id);
            consulta.setString(2, hash);
            consulta.executeUpdate();
        }
    }
}
