package una.eif206.reservas.configuracion;

import una.eif206.reservas.datos.UsuarioMySqlDatos;
import una.eif206.reservas.seguridad.Claves;

/** Ejecutar una sola vez por cuenta, con variables de entorno locales de IntelliJ. */
public final class CrearAdministradorBD {
    private CrearAdministradorBD() { }

    public static void main(String[] args) {
        String id = System.getenv("RESERVAS_ADMIN_ID");
        String clave = System.getenv("RESERVAS_ADMIN_CLAVE");
        if (id == null || id.isBlank() || id.length() > 100 || !id.equals(id.strip())) {
            System.err.println("Configure RESERVAS_ADMIN_ID (1 a 100 caracteres, sin espacios en los extremos).");
            System.exit(1);
            return;
        }
        try {
            new UsuarioMySqlDatos().crearAdministrador(id, Claves.proteger(clave));
            System.out.println("Administrador creado. Ya puede iniciar sesión con su id y clave.");
        } catch (java.sql.SQLException e) {
            System.err.println(e.getErrorCode() == 1062 ? "Ese usuario ya existe. No se modificó su contraseña."
                    : "No se pudo crear el usuario. Revise la conexión y permisos. SQLState: " + e.getSQLState());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Configure RESERVAS_ADMIN_CLAVE: " + e.getMessage());
            System.exit(1);
        }
    }
}
