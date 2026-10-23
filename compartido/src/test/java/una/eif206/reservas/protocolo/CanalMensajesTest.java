package una.eif206.reservas.protocolo;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class CanalMensajesTest {
    @Test void rechazaObjetosAjenosAlProtocolo() throws Exception {
        var buffer = new ByteArrayOutputStream();
        new CanalMensajes(InputStream.nullInputStream(), buffer).enviar(new File("no-debe-deserializarse"));
        var lector = new CanalMensajes(new ByteArrayInputStream(buffer.toByteArray()), OutputStream.nullOutputStream());
        assertThrows(InvalidClassException.class, lector::recibir);
    }
    @Test void rechazaLongitudesInvalidasAntesDeReservarMemoria() throws Exception {
        var buffer = new ByteArrayOutputStream();
        new DataOutputStream(buffer).writeInt(Integer.MAX_VALUE);
        var lector = new CanalMensajes(new ByteArrayInputStream(buffer.toByteArray()), OutputStream.nullOutputStream());
        assertThrows(IOException.class, lector::recibir);
    }
}
