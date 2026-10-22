package una.eif206.reservas.configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private ConexionBD() {
    }

    public static Connection abrir() throws SQLException {
        String url = variable("RESERVAS_DB_URL", "jdbc:mysql://localhost:3306/sistema_reservas");
        String usuario = variable("RESERVAS_DB_USUARIO", "reservas_app");
        String clave = System.getenv("RESERVAS_DB_CLAVE");

        if (clave == null || clave.isBlank()) {
            throw new SQLException(
                    "Falta configurar la variable RESERVAS_DB_CLAVE."
            );
        }

        Properties propiedades = new Properties();
        propiedades.setProperty("user", usuario);
        propiedades.setProperty("password", clave);
        propiedades.setProperty("connectTimeout", "5000");
        propiedades.setProperty("socketTimeout", "10000");
        return DriverManager.getConnection(url, propiedades);
    }

    private static String variable(String nombre, String valorPredeterminado) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPredeterminado : valor;
    }
}
