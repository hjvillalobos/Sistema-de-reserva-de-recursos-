package una.eif206.reservas.cliente;

import una.eif206.reservas.api.ProveedorServicios;

import java.io.IOException;
import una.eif206.reservas.excepciones.ServicioRemotoException;

/** Encuentra la implementación sin importar clases de lógica o persistencia. */
public final class ServiciosCliente {
    private static ProveedorServiciosRemoto proveedor;

    private ServiciosCliente() { }

    public static synchronized ProveedorServicios obtener() {
        if (proveedor == null) {
            try {
                proveedor = new ProveedorServiciosRemoto(
                        System.getProperty("reservas.host", "127.0.0.1"),
                        Integer.getInteger("reservas.puerto", 5050),
                        Integer.getInteger("reservas.esperaMs", 10000));
            } catch (IOException e) {
                throw new ServicioRemotoException("No se pudo conectar al servidor. Compruebe que esté iniciado y que host y puerto sean correctos.", e);
            }
        }
        return proveedor;
    }

    public static synchronized void cerrar() {
        if (proveedor != null) { proveedor.close(); proveedor = null; }
    }
}
