package una.eif206.reservas.configuracion;

import una.eif206.reservas.datos.mysql.Jdbc;
import una.eif206.reservas.seguridad.Claves;
import java.util.UUID;

/** Crea datos nuevos identificados como prueba. No importa ni reemplaza datos existentes. */
public final class PrepararPruebaReservasBD {
    private PrepararPruebaReservasBD() { }
    public static void main(String[] args) throws Exception {
        String clave = System.getenv("RESERVAS_PRUEBA_CLAVE");
        if (clave == null || clave.isBlank()) {
            System.err.println("Configure RESERVAS_PRUEBA_CLAVE para las dos cuentas de prueba.");
            System.exit(1); return;
        }
        String hash = Claves.proteger(clave);
        String codigo = "PRUEBA-" + UUID.randomUUID();
        Jdbc.transaccion(c -> {
            for (String sufijo : new String[]{"-F1", "-F2"}) {
                try (var p = Jdbc.preparar(c, "INSERT INTO usuarios VALUES (?, ?, 'FUNCIONARIO')", codigo + sufijo, hash)) { p.executeUpdate(); }
                try (var p = Jdbc.preparar(c, "INSERT INTO funcionarios VALUES (?, ?, '00000000')", codigo + sufijo, "Funcionario de prueba " + sufijo)) { p.executeUpdate(); }
            }
            try (var p = Jdbc.preparar(c, "INSERT INTO categorias VALUES (?, ?)", codigo + "-CAT", "Categoría de prueba " + codigo)) { p.executeUpdate(); }
            try (var p = Jdbc.preparar(c, "INSERT INTO recursos VALUES (?, ?, ?)", codigo + "-R1", codigo + "-CAT", "Recurso único de prueba")) { p.executeUpdate(); }
            return null;
        });
        System.out.println("Datos de prueba creados en MySQL:");
        System.out.println("Funcionario 1: " + codigo + "-F1");
        System.out.println("Funcionario 2: " + codigo + "-F2");
        System.out.println("Categoría: " + codigo + "-CAT");
        System.out.println("Recurso: " + codigo + "-R1");
        System.out.println("Ambas cuentas usan la clave elegida en RESERVAS_PRUEBA_CLAVE.");
    }
}
