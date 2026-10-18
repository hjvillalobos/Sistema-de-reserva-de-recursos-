package una.eif206.reservas.cliente;

import una.eif206.reservas.api.ProveedorServicios;

import java.util.ServiceLoader;

/** Encuentra la implementación sin importar clases de lógica o persistencia. */
public final class ServiciosCliente {
    private static ProveedorServicios proveedor;

    private ServiciosCliente() { }

    public static synchronized ProveedorServicios obtener() {
        if (proveedor == null) {
            proveedor = ServiceLoader.load(ProveedorServicios.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Todavía no se ha implementado la conexión remota. "
                            + "Para ejecutar la versión XML durante la migración, "
                            + "utilice el perfil Maven 'local' indicado en el README."));
        }
        return proveedor;
    }
}
