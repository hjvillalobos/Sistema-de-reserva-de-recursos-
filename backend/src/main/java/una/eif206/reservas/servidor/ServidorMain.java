package una.eif206.reservas.servidor;

import java.nio.file.*;

public final class ServidorMain {
    public static void main(String[] args) throws Exception {
        String host = System.getProperty("reservas.bind", "127.0.0.1");
        int puerto = Integer.getInteger("reservas.puerto", 5050);
        Path carpeta = Path.of(System.getProperty("reservas.datos", "data")).toAbsolutePath();
        // El servidor real utiliza MySQL para el acceso. No retrocede a claves XML si falla.
        try (var conexion = una.eif206.reservas.configuracion.ConexionBD.abrir();
             var consulta = conexion.prepareStatement("SELECT id, clave_hash, rol FROM usuarios LIMIT 0")) {
            consulta.executeQuery().close();
        }
        try (var servidor = new ServidorReservas(host, puerto,
                new ServiciosServidor(carpeta, new una.eif206.reservas.datos.UsuarioMySqlDatos()))) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { servidor.close(); } catch (Exception ignored) { }
            }));
            System.out.println("Servidor en " + host + ":" + servidor.puerto());
            System.out.println("Acceso, recursos y reservas: MySQL. Categorías y funcionarios: consulta MySQL.");
            servidor.ejecutar();
        }
    }
}
