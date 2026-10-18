package una.eif206.reservas.configuracion;

import org.junit.jupiter.api.Test;
import una.eif206.reservas.api.ProveedorServicios;
import una.eif206.reservas.excepciones.ValidacionException;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorServiciosLocalIT {
    @Test
    void elRegistroDescubreLosServiciosSinAcoplarElClienteAlBackend() {
        ProveedorServicios servicios = ServiceLoader.load(ProveedorServicios.class)
                .findFirst().orElseThrow();
        assertAll(
                () -> assertNotNull(servicios.login()),
                () -> assertNotNull(servicios.reservas()),
                () -> assertNotNull(servicios.recursos()),
                () -> assertNotNull(servicios.categorias()),
                () -> assertNotNull(servicios.funcionarios()),
                () -> assertNotNull(servicios.calendarizacion()),
                () -> assertNotNull(servicios.programacion()),
                () -> assertNotNull(servicios.estadisticas())
        );
        // Atraviesa el contrato hasta la validación real, sin leer archivos XML.
        assertThrows(ValidacionException.class, () -> servicios.login().autenticar("", ""));
    }
}
