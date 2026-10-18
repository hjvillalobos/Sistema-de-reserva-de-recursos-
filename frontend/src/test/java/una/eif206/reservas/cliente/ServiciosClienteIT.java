package una.eif206.reservas.cliente;

import org.junit.jupiter.api.Test;
import una.eif206.reservas.excepciones.ValidacionException;

import static org.junit.jupiter.api.Assertions.*;

class ServiciosClienteIT {
    @Test
    void soloElPerfilLocalPuedeCargarLaImplementacionXml() {
        if (Boolean.getBoolean("reservas.modoLocal")) {
            var servicios = ServiciosCliente.obtener();
            assertThrows(ValidacionException.class, () -> servicios.login().autenticar("", ""));
        } else {
            assertThrows(ClassNotFoundException.class,
                    () -> Class.forName("una.eif206.reservas.datos.ReservaDatos"));
            assertThrows(ClassNotFoundException.class,
                    () -> Class.forName("jakarta.xml.bind.JAXBContext"));
            var error = assertThrows(IllegalStateException.class, ServiciosCliente::obtener);
            assertTrue(error.getMessage().contains("conexión remota"));
        }
    }
}
