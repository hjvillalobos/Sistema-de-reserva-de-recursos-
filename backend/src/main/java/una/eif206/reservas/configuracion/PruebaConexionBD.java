package una.eif206.reservas.configuracion;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

/** Comprobación manual de acceso. No crea tablas ni modifica datos. */
public final class PruebaConexionBD {
    private PruebaConexionBD() {
    }

    public static void main(String[] args) {
        try (var conexion = ConexionBD.abrir();
             var consulta = conexion.createStatement()) {
            consulta.setQueryTimeout(10);
            try (var resultado = consulta.executeQuery("SELECT DATABASE(), VERSION()")) {
                if (!resultado.next()) throw new SQLException("No se recibió información del servidor.");
                System.out.println("Base de datos: " + resultado.getString(1));
                System.out.println("Versión de MySQL: " + resultado.getString(2));
            }

            Set<String> faltantes = new TreeSet<>(Set.of("usuarios", "funcionarios",
                    "categorias", "recursos", "reservas", "reserva_recursos"));
            try (var resultado = consulta.executeQuery("SHOW TABLES")) {
                while (resultado.next()) {
                    String tabla = resultado.getString(1);
                    System.out.println("Tabla: " + tabla);
                    faltantes.remove(tabla);
                }
            }
            if (!faltantes.isEmpty()) {
                throw new SQLException("Faltan tablas esperadas: " + String.join(", ", faltantes));
            }
            System.out.println("Conexión correcta. Las seis tablas están disponibles.");
        } catch (SQLException error) {
            System.err.println("No se pudo completar la prueba: " + error.getMessage());
            System.err.println("SQLState: " + error.getSQLState() + "; código: " + error.getErrorCode());
            System.exit(1);
        }
    }
}
